package flounder.shaders;

import flounder.resources.*;

/**
 * A class that represents a shader type.
 */
public class ShaderType {
	private int shaderType;
	private MyFile shaderFile;
	private String shaderString;
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
		this.shaderFile = shaderFile;
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
		this.shaderString = shaderString;
		this.shaderBuilder = new StringBuilder();
		this.shaderProgramID = -1;
	}

	public int getShaderType() {
		return shaderType;
	}

	protected MyFile getShaderFile() {
		return shaderFile;
	}

	protected String getShaderString() {
		return shaderString;
	}

	public StringBuilder getShaderBuilder() {
		return shaderBuilder;
	}

	public int getShaderProgramID() {
		return shaderProgramID;
	}

	public void setShaderProgramID(int shaderProgramID) {
		this.shaderProgramID = shaderProgramID;
	}
}
