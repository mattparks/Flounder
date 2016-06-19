package flounder.physics.renderer;

import flounder.resources.*;
import flounder.shaders.*;

/**
 * A shader used to render AABB's width.
 */
public class AABBShader extends ShaderProgram {
	private static final MyFile VERTEX_SHADER = new MyFile("flounder/physics/renderer", "aabbVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("flounder/physics/renderer", "aabbFragment.glsl");

	protected UniformMat4 projectionMatrix = new UniformMat4("projectionMatrix");
	protected UniformMat4 viewMatrix = new UniformMat4("viewMatrix");
	protected UniformVec4 clipPlane = new UniformVec4("clipPlane");
	protected UniformMat4 modelMatrix = new UniformMat4("modelMatrix");
	protected UniformVec3 colour = new UniformVec3("colour");

	/**
	 * Creates a AABB renderer shader.
	 */
	protected AABBShader() {
		super("aabb", VERTEX_SHADER, FRAGMENT_SHADER);
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, clipPlane, modelMatrix, colour);
	}
}
