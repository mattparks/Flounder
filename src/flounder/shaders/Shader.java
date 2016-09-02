package flounder.shaders;

import flounder.engine.*;
import flounder.resources.*;

import java.util.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Class that represents a loaded shader.
 */
public class Shader {
	public static final MyFile SHADERS_LOC = new MyFile(MyFile.RES_FOLDER, "shaders");

	private String shaderName;
	private boolean loaded;

	private Map<String, Uniform> uniforms;
	private int programID;

	/**
	 * Creates a new OpenGL shader object.
	 */
	protected Shader() {
		this.loaded = false;
	}

	/**
	 * Creates a new Shader Builder.
	 *
	 * @param shaderName The name of the shader to be loaded.
	 *
	 * @return A new Shader Builder.
	 */
	public static ShaderBuilder newShader(String shaderName) {
		return new ShaderBuilder(shaderName);
	}

	/**
	 * Creates a new empty Shader.
	 *
	 * @return A new empty Shader.
	 */
	public static Shader getEmptyShader() {
		return new Shader();
	}

	/**
	 * Loads data into this shader program.
	 *
	 * @param programID The shader programs OpenGL ID.
	 * @param shaderName The shaders name.
	 * @param uniforms The uniforms loaded from the shaders.
	 */
	protected void loadData(int programID, String shaderName, Map<String, Uniform> uniforms) {
		this.programID = programID;
		this.shaderName = shaderName;
		this.uniforms = uniforms;
		this.loaded = true;
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
	 * Gets if the texture is loaded.
	 *
	 * @return If the texture is loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Gets a uniform from a name.
	 *
	 * @param uniformName The uniforms name.
	 *
	 * @return The uniform that was found.
	 */
	public UniformBool getUniformBool(String uniformName) {
		try {
			return (UniformBool) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a bool!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

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
	public UniformFloat getUniformFloat(String uniformName) {
		try {
			return (UniformFloat) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a float!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

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
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a mat2!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

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
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a mat3!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

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
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a mat4!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

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
	public UniformSampler2D getUniformSampler(String uniformName) {
		try {
			return (UniformSampler2D) uniforms.get(uniformName);
		} catch (ClassCastException e) {
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a sampler!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

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
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a vec2!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

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
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a vec3!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

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
			if (uniforms.get(uniformName) != null) {
				FlounderEngine.getLogger().error(uniformName + " is not a vec4!");
			} else {
				FlounderEngine.getLogger().error("Could not find a uniform for " + uniformName);
			}

			FlounderEngine.getLogger().exception(e);
		}

		return null;
	}

	/**
	 * Deletes the shader, do not start after calling this.
	 */
	public void dispose() {
		FlounderEngine.getProcessors().sendGLRequest(new ShaderDeleteRequest(programID));
		loaded = false;
	}
}
