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
	private boolean initialized;
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
	public ShaderProgram(final String shaderName, final MyFile vertexFile, final MyFile fragmentFile) {
		if (!initialized) {
			layoutLocations = new ArrayList<>();
			layoutBindings = new ArrayList<>();
			this.shaderName = shaderName;
			initShader(loadShader(loadFromFile(vertexFile, true, true), GL_VERTEX_SHADER), loadShader(loadFromFile(fragmentFile, false, true), GL_FRAGMENT_SHADER));
			initialized = true;
		}
	}

	/**
	 * Creates a new shader program with a fragment and vertex shader.
	 *
	 * @param shaderName The name of the shader.
	 * @param vertexString The vertex shader string.
	 * @param fragmentString The fragment shader string.
	 */
	public ShaderProgram(final String shaderName, final String vertexString, final String fragmentString) {
		if (!initialized) {
			layoutLocations = new ArrayList<>();
			layoutBindings = new ArrayList<>();
			this.shaderName = shaderName;
			initShader(loadShader(loadFromString(vertexString, true, true), GL_VERTEX_SHADER), loadShader(loadFromString(fragmentString, false, true), GL_FRAGMENT_SHADER));
			initialized = true;
		}
	}

	private StringBuilder loadFromFile(final MyFile file, final boolean vertexShader, final boolean addToLayouts) {
		final StringBuilder shaderSource = new StringBuilder();

		try {
			final BufferedReader reader = file.getReader();
			String line;

			while ((line = reader.readLine()) != null) {
				shaderSource.append(processShaderLine(line, vertexShader, addToLayouts) + "\n");
			}
		} catch (Exception e) {
			FlounderLogger.error("Could not read file " + file.getName());
			FlounderLogger.exception(e);
			System.exit(-1);
		}

		return shaderSource;
	}

	private StringBuilder loadFromString(final String string, final boolean vertexShader, final boolean addToLayouts) {
		final StringBuilder shaderSource = new StringBuilder();

		for (String line : string.split("\n")) {
			shaderSource.append(processShaderLine(line, vertexShader, addToLayouts) + "\n");
		}

		return shaderSource;
	}

	private StringBuilder processShaderLine(final String line, final boolean vertexShader, final boolean addToLayouts) {
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
			final StringBuilder includedString = loadFromFile(new MyFile(included), true, true);
			return includedString;
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

	private int loadShader(final StringBuilder source, final int type) {
		final int shaderID = glCreateShader(type);
		glShaderSource(shaderID, source);
		glCompileShader(shaderID);

		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			FlounderLogger.error(glGetShaderInfoLog(shaderID, 500));
			FlounderLogger.error("Could not compile shader " + shaderName);
			System.exit(-1);
		}

		return shaderID;
	}

	private int findCharPos(final String line, final char c) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == c) {
				return i;
			}
		}

		return 0;
	}

	private void initShader(final int vertexShaderID, final int fragmentShaderID) {
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);

		for (final String l : layoutLocations) {
			String locationName = l.substring(l.lastIndexOf(" ") + 1, l.length() - 1);
			final int locationValue = Integer.parseInt(l.substring(findCharPos(l, '=') + 1, findCharPos(l, ')')).replaceAll("\\s+", ""));
			glBindAttribLocation(programID, locationValue, locationName);
		}

		glLinkProgram(programID);
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);

		start();

		for (final String b : layoutBindings) {
			String bindingName = b.substring(b.lastIndexOf(" ") + 1, b.length() - 1);
			final int bindingValue = Integer.parseInt(b.substring(findCharPos(b, '=') + 1, findCharPos(b, ')')).replaceAll("\\s+", ""));
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

	public void storeAllUniformLocations(final Uniform... uniforms) {
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
		if (initialized) {
			glUseProgram(0);
			glDeleteProgram(programID);
			initialized = false;
		}
	}
}
