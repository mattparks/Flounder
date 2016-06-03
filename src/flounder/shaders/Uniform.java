package flounder.shaders;

import flounder.engine.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a uniform variable uploaded from Java to OpenGL shaders.
 */
public abstract class Uniform {
	private static final int NOT_FOUND = -1;

	private String name;
	private int location;

	protected Uniform(String name) {
		this.name = name;
	}

	protected void storeUniformLocation(int programID) {
		location = glGetUniformLocation(programID, name);

		if (location == NOT_FOUND) {
			FlounderLogger.error("No uniform variable called " + name + " found!");
		}
	}

	protected int getLocation() {
		return location;
	}
}
