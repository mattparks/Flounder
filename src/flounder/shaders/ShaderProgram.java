package flounder.shaders;

import flounder.engine.*;
import flounder.helpers.*;
import flounder.resources.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a user-defined program designed to run on the graphics processor and manages loading, starting, stopping and cleaning uo.
 */
public class ShaderProgram {
	private List<String> layoutLocations;
	private List<String> layoutBindings;
	private List<Pair<Uniforms, String>> shaderUniforms;

	private String shaderName;
	private int programID;
	private Map<String, Uniform> uniforms;

	/**
	 * Creates a new shader program with a fragment and vertex shader.
	 *
	 * @param shaderName The name of the shader.
	 * @param vertexFile The vertex shader file.
	 * @param fragmentFile The fragment shader file.
	 */
	public ShaderProgram(String shaderName, MyFile vertexFile, MyFile fragmentFile) {
		layoutLocations = new ArrayList<>();
		layoutBindings = new ArrayList<>();
		shaderUniforms = new ArrayList<>();

		this.shaderName = shaderName;

		StringBuilder vertexShader = loadFromFile(vertexFile, ShaderTypes.VERTEX);
		StringBuilder fragmentShader = loadFromFile(fragmentFile, ShaderTypes.FRAGMENT);

		createShader(loadShader(vertexShader, GL_VERTEX_SHADER), loadShader(fragmentShader, GL_FRAGMENT_SHADER));
	}

	/**
	 * Creates a new shader program with a fragment and vertex shader.
	 *
	 * @param shaderName The name of the shader.
	 * @param vertexString The vertex shader string.
	 * @param fragmentString The fragment shader string.
	 */
	public ShaderProgram(String shaderName, String vertexString, String fragmentString) {
		layoutLocations = new ArrayList<>();
		layoutBindings = new ArrayList<>();

		this.shaderName = shaderName;

		StringBuilder vertexShader = loadFromString(vertexString, ShaderTypes.VERTEX);
		StringBuilder fragmentShader = loadFromString(fragmentString, ShaderTypes.FRAGMENT);

		createShader(loadShader(vertexShader, GL_VERTEX_SHADER), loadShader(fragmentShader, GL_FRAGMENT_SHADER));
	}

	private StringBuilder loadFromFile(MyFile file, ShaderTypes shaderType) {
		StringBuilder shaderSource = new StringBuilder();

		try {
			BufferedReader reader = file.getReader();
			String line;

			while ((line = reader.readLine()) != null) {
				shaderSource.append(processShaderLine(line.trim(), shaderType) + "\n");
			}
		} catch (Exception e) {
			FlounderEngine.getLogger().error("Could not read file " + file.getName());
			FlounderEngine.getLogger().exception(e);
			System.exit(-1);
		}

		return shaderSource;
	}

	private StringBuilder loadFromString(String string, ShaderTypes shaderType) {
		StringBuilder shaderSource = new StringBuilder();

		for (String line : string.split("\n")) {
			shaderSource.append(processShaderLine(line.trim(), shaderType) + "\n");
		}

		return shaderSource;
	}

	private StringBuilder processShaderLine(String line, ShaderTypes shaderType) {
		if (line.contains("varying")) {
			if (shaderType == ShaderTypes.VERTEX) {
				return new StringBuilder().append(line.replace("varying", "out"));
			} else {
				return new StringBuilder().append(line.replace("varying", "in"));
			}
		}

		if (line.contains("#include")) {
			String included = line.replaceAll("\\s+", "").replaceAll("\"", "");
			included = included.substring("#include".length(), included.length());
			return loadFromFile(new MyFile(included), ShaderTypes.INCLUDED);
		} else if (line.replaceAll("\\s+", "").startsWith("layout") && shaderType != ShaderTypes.INCLUDED) {
			if (line.contains("location")) {
				layoutLocations.add(line);
				return new StringBuilder().append(line.substring(findCharPos(line, ')') + 1, line.length()));
			} else if (line.contains("binding")) {
				layoutBindings.add(line);
				return new StringBuilder().append(line.substring(findCharPos(line, ')') + 1, line.length()));
			}
		}

		if (line.startsWith("uniform")) {
			String uniformVarName = line.substring("uniform".length() + 1, line.length() - 1);
			String uniform = uniformVarName.split(" ")[0].toUpperCase();
			String name = uniformVarName.split(" ")[1];
			shaderUniforms.add(new Pair<>(Uniforms.valueOf(uniform), name));
		}

		return new StringBuilder().append(line);
	}

	private int loadShader(StringBuilder source, int type) {
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, source);
		glCompileShader(shaderID);

		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			FlounderEngine.getLogger().error(glGetShaderInfoLog(shaderID, 500));
			FlounderEngine.getLogger().error("Could not compile shader " + shaderName);
			System.exit(-1);
		}

		return shaderID;
	}

	private void createShader(int vertexShaderID, int fragmentShaderID) {
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);

		for (String l : layoutLocations) {
			String locationName = l.substring(l.lastIndexOf(" ") + 1, l.length() - 1);
			int locationValue = Integer.parseInt(l.substring(findCharPos(l, '=') + 1, findCharPos(l, ')')).replaceAll("\\s+", ""));
			glBindAttribLocation(programID, locationValue, locationName);
		}

		glLinkProgram(programID);
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);

		start();

		for (String b : layoutBindings) {
			String bindingName = b.substring(b.lastIndexOf(" ") + 1, b.length() - 1);
			int bindingValue = Integer.parseInt(b.substring(findCharPos(b, '=') + 1, findCharPos(b, ')')).replaceAll("\\s+", ""));
			UniformSampler sampler = new UniformSampler(bindingName);
			sampler.storeUniformLocation(programID);
			sampler.loadTexUnit(bindingValue);
		}

		stop();

		// Creates the Uniforms.
		uniforms = new HashMap<>();

		for (Pair<Uniforms, String> pair : shaderUniforms) {
			String uniformClass = pair.getFirst().getUniformClass();
			Uniform uniformObject = null;

			// Loads the uniform from the class name.
			try {
				Class<?> clazz = Class.forName(uniformClass);
				Constructor<?> ctor = clazz.getConstructor(String.class);
				Object object = ctor.newInstance(new Object[]{pair.getSecond()});
				uniformObject = (Uniform) object;
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
				FlounderEngine.getLogger().error("Shader could not create the uniform type of " + uniformClass);
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

		// Validates the GLSL shader.
		glValidateProgram(programID);

		// Layouts not needed anymore.
		layoutLocations.clear();
		layoutBindings.clear();
		shaderUniforms.clear();
	}

	private int findCharPos(String line, char c) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == c) {
				return i;
			}
		}

		return 0;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformFloat getUniformFloat(String uniformName) {
		try {
			return (UniformFloat) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			FlounderEngine.getLogger().error("Could not find a float for " + uniformName);
			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformMat2 getUniformMat2(String uniformName) {
		try {
			return (UniformMat2) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			FlounderEngine.getLogger().error("Could not find a mat2 for " + uniformName);
			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformMat3 getUniformMat3(String uniformName) {
		try {
			return (UniformMat3) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			FlounderEngine.getLogger().error("Could not find a mat3 for " + uniformName);
			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformMat4 getUniformMat4(String uniformName) {
		try {
			return (UniformMat4) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			FlounderEngine.getLogger().error("Could not find a mat4 for " + uniformName);
			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformSampler getUniformSampler(String uniformName) {
		try {
			return (UniformSampler) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			FlounderEngine.getLogger().error("Could not find a sampler for " + uniformName);
			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformVec2 getUniformVec2(String uniformName) {
		try {
			return (UniformVec2) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			FlounderEngine.getLogger().error("Could not find a vec2 for " + uniformName);
			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformVec3 getUniformVec3(String uniformName) {
		try {
			return (UniformVec3) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			FlounderEngine.getLogger().error("Could not find a vec3 for " + uniformName);
			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformVec4 getUniformVec4(String uniformName) {
		try {
			return (UniformVec4) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			FlounderEngine.getLogger().error("Could not find a vec4 for " + uniformName);
			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Starts the shader program.
	 */
	public void start() {
		glUseProgram(programID);
	}

	/**
	 * Stops the shader program.
	 */
	public void stop() {
		glUseProgram(0);
	}

	/**
	 * Deletes the shader, do not start after calling this.
	 */
	public void dispose() {
		glUseProgram(0);
		glDeleteProgram(programID);
	}

	private enum ShaderTypes {
		FRAGMENT, VERTEX, INCLUDED
	}

	private enum Uniforms {
		FLOAT(UniformFloat.class.getName()), MAT2(UniformMat2.class.getName()), MAT3(UniformMat3.class.getName()),
		MAT4(UniformMat4.class.getName()), SAMPLER2D(UniformSampler.class.getName()), VEC2(UniformVec2.class.getName()),
		VEC3(UniformVec3.class.getName()), VEC4(UniformVec4.class.getName());

		private String uniformClass;

		Uniforms(String uniformClass) {
			this.uniformClass = uniformClass;
		}

		public String getUniformClass() {
			return uniformClass;
		}
	}
}
