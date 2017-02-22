package flounder.shaders;

import flounder.factory.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.processing.*;

import java.util.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Class that represents a loaded shader.
 */
public class ShaderObject extends FactoryObject {
	private List<Pair<String, String>> constantValues;
	private List<String> layoutLocations;
	private List<String> layoutBindings;
	private List<Pair<Uniform.Uniforms, String>> shaderUniforms;

	private String name;

	private Map<String, Uniform> uniforms;

	private int programID;

	/**
	 * A new OpenGL shader object.
	 */
	protected ShaderObject() {
		super();
		this.uniforms = null;

		this.name = null;
	}

	protected void loadData(List<Pair<String, String>> constantValues, List<String> layoutLocations, List<String> layoutBindings, List<Pair<Uniform.Uniforms, String>> shaderUniforms, String name) {
		this.constantValues = constantValues;
		this.layoutLocations = layoutLocations;
		this.layoutBindings = layoutBindings;
		this.shaderUniforms = shaderUniforms;

		this.name = name;
	}

	protected void loadGL(Map<String, Uniform> uniforms, int shaderID) {
		this.uniforms = uniforms;

		this.programID = shaderID;
	}

	public List<Pair<String, String>> getConstantValues() {
		return constantValues;
	}

	public List<String> getLayoutLocations() {
		return layoutLocations;
	}

	public List<String> getLayoutBindings() {
		return layoutBindings;
	}

	public List<Pair<Uniform.Uniforms, String>> getShaderUniforms() {
		return shaderUniforms;
	}

	/**
	 * Gets the loaded name for the shader.
	 *
	 * @return The shaders name.
	 */
	public String getName() {
		return name;
	}

	public int getProgramID() {
		return programID;
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
				FlounderLogger.error(uniformName + " is not a bool!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
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
				FlounderLogger.error(uniformName + " is not a float!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
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
				FlounderLogger.error(uniformName + " is not a mat2!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
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
				FlounderLogger.error(uniformName + " is not a mat3!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
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
				FlounderLogger.error(uniformName + " is not a mat4!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
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
				FlounderLogger.error(uniformName + " is not a sampler!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
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
				FlounderLogger.error(uniformName + " is not a vec2!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
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
				FlounderLogger.error(uniformName + " is not a vec3!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
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
				FlounderLogger.error(uniformName + " is not a vec4!");
			} else {
				FlounderLogger.error("Could not find a uniform for " + uniformName);
			}

			FlounderLogger.exception(e);
		}

		return null;
	}

	/**
	 * Deletes the shader from OpenGL memory.
	 */
	public void delete() {
		if (isLoaded()) {
			FlounderProcessors.sendRequest(new ShaderDeleteRequest(this));
			setFullyLoaded(false);
		}
	}
}
