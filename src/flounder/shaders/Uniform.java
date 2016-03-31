package flounder.shaders;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a uniform variable uploaded from Java to OpenGL shaders.
 */
public abstract class Uniform {
	private static final int NOT_FOUND = -1;

	private final String name;
	private int location;

	protected Uniform(final String name) {
		this.name = name;
	}

	protected void storeUniformLocation(final int programID) {
		location = glGetUniformLocation(programID, name);

		if (location == NOT_FOUND) {
			System.err.println("No uniform variable called " + name + " found!");
		}
	}

	protected int getLocation() {
		return location;
	}
}
