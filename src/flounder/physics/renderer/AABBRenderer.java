package flounder.physics.renderer;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.helpers.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * A renderer that is used to render AABB's.
 */
public class AABBRenderer extends IRenderer {
	public static Vector3f ROTATION_REUSABLE = new Vector3f(0, 0, 0);
	public static Vector3f POSITION_REUSABLE = new Vector3f(0, 0, 0);
	public static Vector3f SCALE_REUSABLE = new Vector3f(0, 0, 0);
	public static Matrix4f MODEL_MATRIX_REUSABLE = new Matrix4f();

	private int[] INDICES = {1, 2, 3, 7, 6, 5, 4, 8, 9, 10, 11, 12, 13, 14, 15, 0, 16, 17, 18, 1, 3, 19, 7, 5, 20, 4, 9, 21, 10, 12, 22, 13, 15, 23, 0, 17};
	private float[] VERTICES = {1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f, 0.999999f, 1.0f, 1.000001f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 0.999999f, 1.0f, 1.000001f, 1.0f, -1.0f, 1.0f, 0.999999f, 1.0f, 1.000001f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f};
	private int VAO;

	private AABBShader shader;
	private int aabbCount;
	private boolean lastWireframe;

	/**
	 * Creates a new AABB renderer.
	 */
	public AABBRenderer() {
		shader = new AABBShader();
		aabbCount = 0;
		lastWireframe = false;

		VAO = FlounderEngine.getLoader().createVAO();
		FlounderEngine.getLoader().createIndicesVBO(VAO, INDICES);
		FlounderEngine.getLoader().storeDataInVBO(VAO, VERTICES, 0, 3);
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		prepareRendering(clipPlane, camera);

		for (AABB aabb : FlounderEngine.getAABBs().getRenderAABB()) {
			renderAABB(aabb);
		}

		endRendering();

		if (FlounderEngine.getProfiler().isOpen()) {
			FlounderEngine.getProfiler().add("AABB", "Render Time", super.getRenderTimeMs());
		}

		aabbCount = 0;
	}

	private void prepareRendering(Vector4f clipPlane, ICamera camera) {
		shader.start();
		shader.projectionMatrix.loadMat4(FlounderEngine.getProjectionMatrix());
		shader.viewMatrix.loadMat4(camera.getViewMatrix());
		shader.clipPlane.loadVec4(clipPlane);

		lastWireframe = OpenGlUtils.isInWireframe();

		OpenGlUtils.antialias(FlounderEngine.getDevices().getDisplay().isAntialiasing());
		OpenGlUtils.cullBackFaces(false);
		OpenGlUtils.goWireframe(true);
		OpenGlUtils.enableDepthTesting();

		OpenGlUtils.bindVAO(VAO, 0);
	}

	private void renderAABB(AABB aabb) {
		Vector3f.add(aabb.getMaxExtents(), aabb.getMinExtents(), POSITION_REUSABLE);
		POSITION_REUSABLE.set(POSITION_REUSABLE.x / 2.0f, POSITION_REUSABLE.y / 2.0f, POSITION_REUSABLE.z / 2.0f);

		ROTATION_REUSABLE.set(0.0f, 0.0f, 0.0f);

		Vector3f.subtract(aabb.getMaxExtents(), aabb.getMinExtents(), SCALE_REUSABLE);
		SCALE_REUSABLE.set(SCALE_REUSABLE.x / 2.0f, SCALE_REUSABLE.y / 2.0f, SCALE_REUSABLE.z / 2.0f);

		MODEL_MATRIX_REUSABLE.setIdentity();
		Matrix4f.transformationMatrix(POSITION_REUSABLE, ROTATION_REUSABLE, SCALE_REUSABLE, MODEL_MATRIX_REUSABLE);

		shader.modelMatrix.loadMat4(MODEL_MATRIX_REUSABLE);
		shader.colour.loadVec3(POSITION_REUSABLE.normalize());

		glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
	}

	private void endRendering() {
		OpenGlUtils.goWireframe(lastWireframe);

		OpenGlUtils.unbindVAO(0);
		shader.stop();

		aabbCount++;
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}