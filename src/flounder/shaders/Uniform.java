package flounder.shaders;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a uniform variable uploaded from Java to OpenGL shaders.
 */
public abstract class Uniform {
	private static final int NOT_FOUND = -1;

	private final String m_name;
	private int m_location;

	protected Uniform(final String name) {
		m_name = name;
	}

	protected void storeUniformLocation(final int programID) {
		m_location = glGetUniformLocation(programID, m_name);

		if (m_location == NOT_FOUND) {
			System.err.println("No uniform variable called " + m_name + " found!");
		}
	}

	protected final int getLocation() {
		return m_location;
	}
}
