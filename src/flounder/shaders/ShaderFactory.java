package flounder.shaders;

import flounder.factory.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.resources.*;

import java.io.*;
import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * A class that represents a factory for loading shaders.
 */
public class ShaderFactory extends Factory {
	private static final ShaderFactory INSTANCE = new ShaderFactory();

	private ShaderFactory() {
		super("shader");
	}

	/**
	 * Gets a new builder to be used to create information for build a object from.
	 *
	 * @return A new factory builder.
	 */
	public static ShaderBuilder newBuilder() {
		return new ShaderBuilder(INSTANCE);
	}

	@Override
	public ShaderObject newObject() {
		return new ShaderObject();
	}

	@Override
	public void loadData(FactoryObject object, FactoryBuilder builder, String name) {
		ShaderBuilder b = (ShaderBuilder) builder;
		ShaderObject o = (ShaderObject) object;

		List<Pair<String, String>> constantValues = new ArrayList<>();
		List<String> layoutLocations = new ArrayList<>();
		List<String> layoutBindings = new ArrayList<>();
		List<Pair<Uniform.Uniforms, String>> shaderUniforms = new ArrayList<>();

		for (ShaderType type : b.getTypes()) {
			if (type.getShaderFile().isPresent()) {
				MyFile file = type.getShaderFile().get();

				try {
					BufferedReader reader = file.getReader();
					String line;

					while ((line = reader.readLine()) != null) {
						type.getShaderBuilder().append(INSTANCE.processShaderLine(line.trim(), constantValues, layoutLocations, layoutBindings, shaderUniforms, type.getShaderType()) + "\n");
					}
				} catch (Exception e) {
					FlounderLogger.error("Could not read file " + file.getName());
					FlounderLogger.exception(e);
					System.exit(-1);
				}
			} else if (type.getShaderString().isPresent()) {
				String string = type.getShaderString().get();

				for (String line : string.split("\n")) {
					type.getShaderBuilder().append(INSTANCE.processShaderLine(line.trim(), constantValues, layoutLocations, layoutBindings, shaderUniforms, type.getShaderType()) + "\n");
				}
			}
		}

		o.loadData(constantValues, layoutLocations, layoutBindings, shaderUniforms, name);
	}

	private StringBuilder processShaderLine(String line, List<Pair<String, String>> constantValues, List<String> layoutLocations, List<String> layoutBindings, List<Pair<Uniform.Uniforms, String>> shaderUniforms, int shaderType) {
		if (line.contains("#include")) {
			String included = line.replaceAll("\\s+", "").replaceAll("\"", "");
			included = included.substring("#include".length(), included.length());
			MyFile includeFile = new MyFile(FlounderShaders.SHADERS_LOC, included);
			StringBuilder includeSource = new StringBuilder();

			try {
				BufferedReader reader = includeFile.getReader();
				String includeLine;

				while ((includeLine = reader.readLine()) != null) {
					includeSource.append(processShaderLine(includeLine.trim(), constantValues, layoutLocations, layoutBindings, shaderUniforms, shaderType) + "\n");
				}
			} catch (Exception e) {
				FlounderLogger.error("Could not read file " + includeFile.getName());
				FlounderLogger.exception(e);
				System.exit(-1);
			}

			return includeSource;
		} else if (line.replaceAll("\\s+", "").startsWith("layout") && shaderType != -1) {
			if (line.contains("location")) {
				layoutLocations.add(line);
				return new StringBuilder().append(line.substring(findCharPos(line, ')') + 1, line.length()));
			} else if (line.contains("binding")) {
				layoutBindings.add(line);
				return new StringBuilder().append(line.substring(findCharPos(line, ')') + 1, line.length()));
			}
		}

		if (line.startsWith("const")) {
			String uniformVarName = line.substring("const".length() + 1, line.length() - 1);
			uniformVarName = uniformVarName.substring(findCharPos(uniformVarName, ' '), uniformVarName.length()).trim();
			constantValues.add(new Pair<>(uniformVarName.split("=")[0].trim(), uniformVarName.split("=")[1].trim()));
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
					for (Pair<String, String> pair : constantValues) {
						if (pair.getFirst().equals(arraySize)) {
							size = Integer.parseInt(pair.getSecond());
							break;
						}
					}
				}

				for (int i = 0; i < size; i++) {
					shaderUniforms.add(new Pair<>(Uniform.Uniforms.valueOf(uniform), nameArray + "[" + i + "]"));
				}
			} else {
				// Normal Uniforms.
				shaderUniforms.add(new Pair<>(Uniform.Uniforms.valueOf(uniform), name));
			}
		}

		return new StringBuilder().append(line);
	}

	@Override
	protected void create(FactoryObject object, FactoryBuilder builder) {
		// Takes OpenGL compatible data and loads it to the GPU and factory object.
		ShaderBuilder b = (ShaderBuilder) builder;
		ShaderObject o = (ShaderObject) object;

		int programID = glCreateProgram();

		for (ShaderType type : b.getTypes()) {
			int shaderID = glCreateShader(type.getShaderType());
			glShaderSource(shaderID, type.getShaderBuilder());
			glCompileShader(shaderID);

			if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
				FlounderLogger.error(glGetShaderInfoLog(shaderID, 500));
				throw new RuntimeException("Could not compile shader " + type);
			}

			type.setShaderProgramID(shaderID);
			glAttachShader(programID, shaderID);
		}

		for (String location : o.getLayoutLocations()) {
			String locationName = location.substring(location.lastIndexOf(" ") + 1, location.length() - 1);
			String type = location.substring(0, location.lastIndexOf(" ") + 1);
			type = type.substring(location.lastIndexOf(")") + 1, type.length()).trim();
			int locationValue = Integer.parseInt(location.substring(findCharPos(location, '=') + 1, findCharPos(location, ')')).replaceAll("\\s+", ""));

			if (type.contains("in")) {
				glBindAttribLocation(programID, locationValue, locationName);
			} else if (type.contains("out")) {
				glBindFragDataLocation(programID, locationValue, locationName);
			} else {
				FlounderLogger.error("Could not find location type of: " + type);
			}
		}

		glLinkProgram(programID);

		for (ShaderType type : b.getTypes()) {
			glDetachShader(programID, type.getShaderProgramID());
			glDeleteShader(type.getShaderProgramID());
			type.setShaderProgramID(-1);
		}

		glUseProgram(programID);

		for (String binding : o.getLayoutBindings()) {
			String bindingName = binding.substring(binding.lastIndexOf(" ") + 1, binding.length() - 1);
			int bindingValue = Integer.parseInt(binding.substring(findCharPos(binding, '=') + 1, findCharPos(binding, ')')).replaceAll("\\s+", ""));
			UniformSampler2D sampler = new UniformSampler2D(bindingName, o);
			sampler.storeUniformLocation(programID);
			sampler.loadTexUnit(bindingValue);
		}

		glUseProgram(0);

		Map<String, Uniform> uniforms = new HashMap<>();

		for (Pair<Uniform.Uniforms, String> pair : o.getShaderUniforms()) {
			String uniformClass = pair.getFirst().getUniformClass();
			Uniform uniformObject = null;

			// Loads the uniform from the class name.
			try {
				Class<?> clazz = Class.forName(uniformClass);
				Constructor<?> ctor = clazz.getConstructor(String.class, ShaderObject.class);
				Object uobject = ctor.newInstance(new Object[]{pair.getSecond(), o});
				uniformObject = (Uniform) uobject;
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

		o.loadGL(uniforms, programID);
	}

	private int findCharPos(String line, char c) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == c) {
				return i;
			}
		}

		return 0;
	}

	@Override
	public Map<String, SoftReference<FactoryObject>> getLoaded() {
		return FlounderShaders.getLoaded();
	}
}
