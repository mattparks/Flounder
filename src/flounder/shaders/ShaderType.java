package flounder.shaders;

import flounder.resources.*;

import java.util.*;

/**
 * A class that represents a shader type.
 */
public class ShaderType {
	private int shaderType;
	private Optional<MyFile> shaderFile;
	private Optional<String> shaderString;
	private StringBuilder shaderBuilder;
	private int shaderProgramID;

	/**
	 * Creates a new shader type from a file.
	 *
	 * @param shaderType The type of OpenGL shader.
	 * @param shaderFile The file to load from.
	 */
	public ShaderType(int shaderType, MyFile shaderFile) {
		this.shaderType = shaderType;
		this.shaderFile = Optional.of(shaderFile);
		this.shaderString = null;
		this.shaderBuilder = new StringBuilder();
		this.shaderProgramID = -1;
	}

	/**
	 * Creates a new shader type from a string.
	 *
	 * @param shaderType The type of OpenGL shader.
	 * @param shaderString The string to load from.
	 */
	public ShaderType(int shaderType, String shaderString) {
		this.shaderType = shaderType;
		this.shaderFile = null;
		this.shaderString = Optional.of(shaderString);
		this.shaderBuilder = new StringBuilder();
		this.shaderProgramID = -1;
	}

	protected int getShaderType() {
		return shaderType;
	}

	protected Optional<MyFile> getShaderFile() {
		return shaderFile;
	}

	protected Optional<String> getShaderString() {
		return shaderString;
	}

	protected StringBuilder getShaderBuilder() {
		return shaderBuilder;
	}

	protected int getShaderProgramID() {
		return shaderProgramID;
	}

	protected void setShaderProgramID(int shaderProgramID) {
		this.shaderProgramID = shaderProgramID;
	}
}
