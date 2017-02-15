package flounder.shaders;

import flounder.logger.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a uniform variable uploaded from Java to OpenGL shaders.
 */
public abstract class Uniform {
	private static final int NOT_FOUND = -1;

	private String name;
	private ShaderObject shader;

	private int location;

	protected Uniform(String name, ShaderObject shader) {
		this.name = name;
		this.shader = shader;
	}

	protected void storeUniformLocation(int programID) {
		location = glGetUniformLocation(programID, name);

		if (location == NOT_FOUND) {
			FlounderLogger.error("No uniform variable called " + name + " found in shader " + shader.getName() + "!");
		}
	}

	protected int getLocation() {
		return location;
	}

	public enum Uniforms {
		BOOL(UniformBool.class.getName()), FLOAT(UniformFloat.class.getName()), SAMPLER2D(UniformSampler2D.class.getName()),
		MAT2(UniformMat2.class.getName()), MAT3(UniformMat3.class.getName()), MAT4(UniformMat4.class.getName()),
		VEC2(UniformVec2.class.getName()), VEC3(UniformVec3.class.getName()), VEC4(UniformVec4.class.getName());

		private String uniformClass;

		Uniforms(String uniformClass) {
			this.uniformClass = uniformClass;
		}

		public String getUniformClass() {
			return uniformClass;
		}
	}
}
