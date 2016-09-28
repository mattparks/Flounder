package flounder.shaders;

import flounder.engine.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.processing.*;
import flounder.profiling.*;
import flounder.resources.*;

import java.io.*;
import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Class capable of loading OBJ files into shaders.
 */
public class FlounderShaders extends IModule {
	private final static FlounderShaders instance = new FlounderShaders();

	private Map<String, SoftReference<Shader>> loaded;

	/**
	 * Creates the engines shader loader.
	 */
	public FlounderShaders() {
		super(ModuleUpdate.BEFORE_ENTRANCE, FlounderLogger.class, FlounderProfiler.class, FlounderProcessors.class);
	}

	@Override
	public void init() {
		this.loaded = new HashMap<>();
	}

	@Override
	public void run() {
	}

	@Override
	public void profile() {
	}

	/**
	 * Creates the shader data using builder configs.
	 *
	 * @param builder The builder configs.
	 *
	 * @return The shader data.
	 */
	public static ShaderData loadShader(ShaderBuilder builder) {
		ShaderData data = new ShaderData();

		for (ShaderType shaderType : builder.getShaderTypes()) {
			if (shaderType.getShaderFile().isPresent()) {
				MyFile file = shaderType.getShaderFile().get();

				try {
					BufferedReader reader = file.getReader();
					String line;

					while ((line = reader.readLine()) != null) {
						shaderType.getShaderBuilder().append(instance.processShaderLine(line.trim(), data, shaderType.getShaderType()) + "\n");
					}
				} catch (Exception e) {
					FlounderLogger.error("Could not read file " + file.getName());
					FlounderLogger.exception(e);
					System.exit(-1);
				}
			} else if (shaderType.getShaderString().isPresent()) {
				String string = shaderType.getShaderString().get();

				for (String line : string.split("\n")) {
					shaderType.getShaderBuilder().append(instance.processShaderLine(line.trim(), data, shaderType.getShaderType()) + "\n");
				}
			}
		}

		return data;
	}

	private StringBuilder processShaderLine(String line, ShaderData data, int shaderType) {
		if (line.contains("#include")) {
			String included = line.replaceAll("\\s+", "").replaceAll("\"", "");
			included = included.substring("#include".length(), included.length());
			MyFile includeFile = new MyFile(Shader.SHADERS_LOC, included);
			StringBuilder includeSource = new StringBuilder();

			try {
				BufferedReader reader = includeFile.getReader();
				String includeLine;

				while ((includeLine = reader.readLine()) != null) {
					includeSource.append(processShaderLine(includeLine.trim(), data, shaderType) + "\n");
				}
			} catch (Exception e) {
				FlounderLogger.error("Could not read file " + includeFile.getName());
				FlounderLogger.exception(e);
				System.exit(-1);
			}

			return includeSource;
		} else if (line.replaceAll("\\s+", "").startsWith("layout") && shaderType != -1) {
			if (line.contains("location")) {
				data.layoutLocations.add(line);
				return new StringBuilder().append(line.substring(findCharPos(line, ')') + 1, line.length()));
			} else if (line.contains("binding")) {
				data.layoutBindings.add(line);
				return new StringBuilder().append(line.substring(findCharPos(line, ')') + 1, line.length()));
			}
		}

		if (line.startsWith("const")) {
			String uniformVarName = line.substring("const".length() + 1, line.length() - 1);
			uniformVarName = uniformVarName.substring(findCharPos(uniformVarName, ' '), uniformVarName.length()).trim();
			data.conatantValues.add(new Pair<>(uniformVarName.split("=")[0].trim(), uniformVarName.split("=")[1].trim()));
		}

		if (line.startsWith("uniform")) {
			String uniformVarName = line.substring("uniform".length() + 1, line.length() - 1);
			String uniform = uniformVarName.split(" ")[0].toUpperCase();
			String name = uniformVarName.split(" ")[1];

			// Array Uniforms.
			if (name.contains("[") && name.contains("]")) {
				String nameArray = name.substring(0, findCharPos(name, '[')).trim();
				String arraySize = name.substring(findCharPos(name, '[') + 1, name.length() - 1).trim();
				int size = 0;

				if (ByteWork.isInteger(arraySize)) {
					size = Integer.parseInt(arraySize);
				} else {
					for (Pair<String, String> pair : data.conatantValues) {
						if (pair.getFirst().equals(arraySize)) {
							size = Integer.parseInt(pair.getSecond());
							break;
						}
					}
				}

				for (int i = 0; i < size; i++) {
					data.shaderUniforms.add(new Pair<>(ShaderData.Uniforms.valueOf(uniform), nameArray + "[" + i + "]"));
				}
			} else {
				// Normal Uniforms.
				data.shaderUniforms.add(new Pair<>(ShaderData.Uniforms.valueOf(uniform), name));
			}
		}

		return new StringBuilder().append(line);
	}

	/**
	 * Loads shader data, and builder data, into a shader.
	 *
	 * @param shader The shader to load into.
	 * @param data The data to use when creating the shader.
	 * @param builder The builder configured for the shader.
	 */
	public static void loadShaderToOpenGL(Shader shader, ShaderData data, ShaderBuilder builder) {
		int programID = glCreateProgram();

		for (ShaderType shaderType : builder.getShaderTypes()) {
			int shaderID = glCreateShader(shaderType.getShaderType());
			glShaderSource(shaderID, shaderType.getShaderBuilder());
			glCompileShader(shaderID);

			if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
				FlounderLogger.error(glGetShaderInfoLog(shaderID, 500));
				FlounderLogger.error("Could not compile shader " + builder.getShaderName());
				System.exit(-1);
			}

			shaderType.setShaderProgramID(shaderID);
			glAttachShader(programID, shaderID);
		}

		instance.loadLocations(programID, data);
		glLinkProgram(programID);

		for (ShaderType shaderType : builder.getShaderTypes()) {
			glDetachShader(programID, shaderType.getShaderProgramID());
			glDeleteShader(shaderType.getShaderProgramID());
			shaderType.setShaderProgramID(-1);
		}

		instance.loadBindings(programID, data);

		shader.loadData(programID, builder.getShaderName(), builder.getShaderTypes(), instance.createUniforms(programID, data));
		data.destroy();
	}

	private void loadLocations(int programID, ShaderData data) {
		for (String l : data.layoutLocations) {
			String locationName = l.substring(l.lastIndexOf(" ") + 1, l.length() - 1);
			String type = l.substring(0, l.lastIndexOf(" ") + 1);
			type = type.substring(l.lastIndexOf(")") + 1, type.length()).trim();
			int locationValue = Integer.parseInt(l.substring(findCharPos(l, '=') + 1, findCharPos(l, ')')).replaceAll("\\s+", ""));

			if (type.contains("in")) {
				glBindAttribLocation(programID, locationValue, locationName);
			} else if (type.contains("out")) {
				glBindFragDataLocation(programID, locationValue, locationName);
			} else {
				FlounderLogger.error("Could not find location type of: " + type);
			}
		}
	}

	private void loadBindings(int programID, ShaderData data) {
		glUseProgram(programID);

		for (String b : data.layoutBindings) {
			String bindingName = b.substring(b.lastIndexOf(" ") + 1, b.length() - 1);
			int bindingValue = Integer.parseInt(b.substring(findCharPos(b, '=') + 1, findCharPos(b, ')')).replaceAll("\\s+", ""));
			UniformSampler2D sampler = new UniformSampler2D(bindingName);
			sampler.storeUniformLocation(programID);
			sampler.loadTexUnit(bindingValue);
		}

		glUseProgram(0);
	}

	private Map<String, Uniform> createUniforms(int programID, ShaderData data) {
		Map<String, Uniform> uniforms = new HashMap<>();

		for (Pair<ShaderData.Uniforms, String> pair : data.shaderUniforms) {
			String uniformClass = pair.getFirst().getUniformClass();
			Uniform uniformObject = null;

			// Loads the uniform from the class name.
			try {
				Class<?> clazz = Class.forName(uniformClass);
				Constructor<?> ctor = clazz.getConstructor(String.class);
				Object object = ctor.newInstance(new Object[]{pair.getSecond()});
				uniformObject = (Uniform) object;
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
				FlounderLogger.error("Shader could not create the uniform type of " + uniformClass);
				e.printStackTrace();
			}

			// If the uniform was loaded.
			if (uniformObject != null) {
				// Store uniform locations.
				uniformObject.storeUniformLocation(programID);

				// Keeps the uniform variables for later usage.
				uniforms.put(pair.getSecond(), uniformObject);
			}
		}

		return uniforms;
	}

	private static int findCharPos(String line, char c) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == c) {
				return i;
			}
		}

		return 0;
	}

	/**
	 * Gets a list of loaded shaders.
	 *
	 * @return A list of loaded shaders.
	 */
	public static Map<String, SoftReference<Shader>> getLoaded() {
		return instance.loaded;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		// TODO: Delete shaders.
	}
}
