package flounder.shaders;

import flounder.engine.*;
import flounder.resources.*;
import org.lwjgl.opengl.*;

import java.io.*;
import java.util.*;

/**
 * Represents a user-defined program designed to run on the graphics processor and manages loading, starting, stopping and cleaning uo.
 */
public class ShaderProgram {
	private boolean initialized;
	private List<String> layoutLocations;
	private List<String> layoutBindings;
	private String shaderName;
	private int programID;

	// TODO: Load everything to StringBuilder from files / hard code.

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
			initShader(loadShader(loadFromFile(vertexFile), true, GL20.GL_VERTEX_SHADER), loadShader(loadFromFile(fragmentFile), false, GL20.GL_FRAGMENT_SHADER));
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
			initShader(loadShader(loadFromString(vertexString), true, GL20.GL_VERTEX_SHADER), loadShader(loadFromString(fragmentString), false, GL20.GL_FRAGMENT_SHADER));
			initialized = true;
		}
	}

	private StringBuilder loadFromFile(final MyFile file) {
		StringBuilder shaderSource = new StringBuilder();

		try {
			BufferedReader reader = file.getReader();
			String line;

			while ((line = reader.readLine()) != null) {
				shaderSource.append(line + "\n");
			}
		} catch (Exception e) {
			Logger.error("Could not read file " + file.getName());
			e.printStackTrace();
			System.exit(-1);
		}

		return shaderSource;
	}

	private StringBuilder loadFromString(final String string) {
		StringBuilder shaderSource = new StringBuilder();

		for (String s : string.split("\n")) {
			shaderSource.append(s + "\n");
		}

		return shaderSource;
	}

	private int loadShader(final StringBuilder source, final boolean vertexShader, final int type) {
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, readShader(source, vertexShader, true));
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Logger.error(GL20.glGetShaderInfoLog(shaderID, 500));
			Logger.error("Could not compile shader " + shaderName);
			System.exit(-1);
		}

		return shaderID;
	}

	private StringBuilder readShader(final StringBuilder source, final boolean vertexShader, final boolean addToLayouts) {
		StringBuilder shaderSource = new StringBuilder();

		for (String line : source.toString().split("\n")) {
			if (line.contains("varying")) {
				if (vertexShader) {
					line = line.replace("varying", "out");
				} else {
					line = line.replace("varying", "in");
				}
			}

			if (line.contains("#include")) {
				String included = line.replaceAll("\\s+", "").replaceAll("\"", "");
				included = included.substring("#include".length(), included.length());
				StringBuilder includedString = readShader(loadFromFile(new MyFile(included)), true, false);
				shaderSource.append(includedString);
			} else if (line.replaceAll("\\s+", "").startsWith("layout") && addToLayouts) {
				if (line.contains("location")) {
					layoutLocations.add(line);
					shaderSource.append(line.substring(findCharPos(line, ')') + 1, line.length())).append("//\n");
				} else if (line.contains("binding")) {
					layoutBindings.add(line);
					shaderSource.append(line.substring(findCharPos(line, ')') + 1, line.length())).append("//\n");
				}
			} else {
				shaderSource.append(line).append("//\n");
			}
		}

		return shaderSource;
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
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		for (String l : layoutLocations) {
			String locationName = l.substring(l.lastIndexOf(" ") + 1, l.length() - 1);
			int locationValue = Integer.parseInt(l.substring(findCharPos(l, '=') + 1, findCharPos(l, ')')).replaceAll("\\s+", ""));
			GL20.glBindAttribLocation(programID, locationValue, locationName);
		}

		GL20.glLinkProgram(programID);
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);

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

	public void storeAllUniformLocations(final Uniform... uniforms) {
		for (Uniform uniform : uniforms) {
			uniform.storeUniformLocation(programID);
		}

		GL20.glValidateProgram(programID);
	}

	/**
	 * Starts the shader program.
	 */
	public void start() {
		GL20.glUseProgram(programID);
	}

	/**
	 * Stops the shader program.
	 */
	public void stop() {
		GL20.glUseProgram(0);
	}

	/**
	 * Deletes the shader, do not start after calling this.
	 */
	public void dispose() {
		if (initialized) {
			GL20.glUseProgram(0);
			GL20.glDeleteProgram(programID);
			initialized = false;
		}
	}
}
