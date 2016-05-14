package flounder.physics;

import flounder.resources.*;
import flounder.shaders.*;

public class AABBShader extends ShaderProgram {
	private static final MyFile VERTEX_SHADER = new MyFile("flounder/physics", "aabbVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("flounder/physics", "aabbFragment.glsl");

	protected final UniformMat4 projectionMatrix = new UniformMat4("projectionMatrix");
	protected final UniformMat4 viewMatrix = new UniformMat4("viewMatrix");
	protected final UniformVec4 clipPlane = new UniformVec4("clipPlane");

	protected AABBShader() {
		super("aabb", VERTEX_SHADER, FRAGMENT_SHADER);
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, clipPlane);
	}
}
