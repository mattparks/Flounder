package flounder.shaders;

import flounder.resources.*;
import org.lwjgl.opengl.*;

import java.io.*;
import java.util.*;

/**
 * Represents a user-defined program designed to run on the graphics processor and manages loading, starting, stopping and cleaning uo.
 */
public class ShaderProgram {
	private List<String> layoutLocations;
	private List<String> layoutBindings;
	private int programID;

	// TODO: Load everything to StringBuilder from files / hard code.

	/**
	 * Creates a new shader program with a fragment and vertex shader.
	 *
	 * @param vertexFile The vertex shader file.
	 * @param fragmentFile The fragment shader file.
	 */
	public ShaderProgram(final MyFile vertexFile, final MyFile fragmentFile) {
		layoutLocations = new ArrayList<>();
		layoutBindings = new ArrayList<>();
		int vertexShaderID = loadShader(vertexFile, true, GL20.GL_VERTEX_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, false, GL20.GL_FRAGMENT_SHADER);
		initShader(vertexShaderID, fragmentShaderID);
	}

	private int loadShader(final MyFile file, final boolean vertexShader, final int type) {
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, readShader(file, vertexShader, true));
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader " + file);
			System.exit(-1);
		}

		return shaderID;
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
	}

	private StringBuilder readShader(final MyFile file, final boolean vertexShader, final boolean addToLayouts) {
		StringBuilder shaderSource = new StringBuilder();

		try {
			BufferedReader reader = file.getReader();
			String line;

			while ((line = reader.readLine()) != null) {
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
					StringBuilder includedString = readShader(new MyFile(included), true, false);
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

			reader.close();
		} catch (Exception e) {
			System.err.println("Could not read file " + file);
			e.printStackTrace();
			System.exit(-1);
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
		GL20.glUseProgram(0);
		GL20.glDeleteProgram(programID);
	}
}
