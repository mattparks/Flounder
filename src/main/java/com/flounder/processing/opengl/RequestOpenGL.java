package com.flounder.processing.opengl;

/**
 * Interface for executable OpenGL requests.
 */
@FunctionalInterface
public interface RequestOpenGL {
	/**
	 * Executed when the request is being processed.
	 */
	void executeRequestGL();
}
