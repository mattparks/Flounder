package flounder.shaders;

import flounder.engine.*;
import flounder.resources.*;

import java.io.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a user-defined program designed to run on the graphics processor and manages loading, starting, stopping and cleaning uo.
 */
public class ShaderProgram {
	private List<String> layoutLocations;
	private List<String> layoutBindings;

	private String shaderName;
	private int programID;

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

		this.shaderName = shaderName;
		createShader(loadShader(loadFromFile(vertexFile, true, true), GL_VERTEX_SHADER), loadShader(loadFromFile(fragmentFile, false, true), GL_FRAGMENT_SHADER));
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
		createShader(loadShader(loadFromString(vertexString, true, true), GL_VERTEX_SHADER), loadShader(loadFromString(fragmentString, false, true), GL_FRAGMENT_SHADER));
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

		// Layouts not needed anymore.
		layoutLocations.clear();
		layoutLocations = null;
		layoutBindings.clear();
		layoutBindings = null;
	}

	private StringBuilder loadFromFile(MyFile file, boolean vertexShader, boolean addToLayouts) {
		StringBuilder shaderSource = new StringBuilder();

		try {
			BufferedReader reader = file.getReader();
			String line;

			while ((line = reader.readLine()) != null) {
				shaderSource.append(processShaderLine(line, vertexShader, addToLayouts) + "\n");
			}
		} catch (Exception e) {
			FlounderEngine.getLogger().error("Could not read file " + file.getName());
			FlounderEngine.getLogger().exception(e);
			System.exit(-1);
		}

		return shaderSource;
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

	private StringBuilder loadFromString(String string, boolean vertexShader, boolean addToLayouts) {
		StringBuilder shaderSource = new StringBuilder();

		for (String line : string.split("\n")) {
			shaderSource.append(processShaderLine(line, vertexShader, addToLayouts) + "\n");
		}

		return shaderSource;
	}

	private StringBuilder processShaderLine(String line, boolean vertexShader, boolean addToLayouts) {
		if (line.contains("varying")) {
			if (vertexShader) {
				return new StringBuilder().append(line.replace("varying", "out"));
			} else {
				return new StringBuilder().append(line.replace("varying", "in"));
			}
		}

		if (line.contains("#include")) {
			String included = line.replaceAll("\\s+", "").replaceAll("\"", "");
			included = included.substring("#include".length(), included.length());
			return loadFromFile(new MyFile(included), true, true);
		} else if (line.replaceAll("\\s+", "").startsWith("layout") && addToLayouts) {
			if (line.contains("location")) {
				layoutLocations.add(line);
				return new StringBuilder().append(line.substring(findCharPos(line, ')') + 1, line.length()));
			} else if (line.contains("binding")) {
				layoutBindings.add(line);
				return new StringBuilder().append(line.substring(findCharPos(line, ')') + 1, line.length()));
			}
		}

		return new StringBuilder().append(line);
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
	 * Stores all uniforms locations.
	 *
	 * @param uniforms The uniforms to store the locations of.
	 */
	public void storeAllUniformLocations(Uniform... uniforms) {
		for (Uniform uniform : uniforms) {
			uniform.storeUniformLocation(programID);
		}

		glValidateProgram(programID);
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
}
