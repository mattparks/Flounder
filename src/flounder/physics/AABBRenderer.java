package flounder.physics;

import flounder.devices.*;
import flounder.engine.*;
import flounder.engine.profiling.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

import static org.lwjgl.opengl.GL11.*;

public class AABBRenderer extends IRenderer {
	public static final Vector3f ROTATION_REUSABLE = new Vector3f(0, 0, 0);
	public static final Vector3f POSITION_REUSABLE = new Vector3f(0, 0, 0);
	public static final Vector3f SCALE_REUSABLE = new Vector3f(0, 0, 0);
	public static final Matrix4f MODEL_MATRIX_REUSABLE = new Matrix4f();

	private final int[] INDICES = {1, 2, 3, 7, 6, 5, 4, 8, 9, 10, 11, 12, 13, 14, 15, 0, 16, 17, 18, 1, 3, 19, 7, 5, 20, 4, 9, 21, 10, 12, 22, 13, 15, 23, 0, 17};
	private final float[] VERTICES = {1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f, 0.999999f, 1.0f, 1.000001f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 0.999999f, 1.0f, 1.000001f, 1.0f, -1.0f, 1.0f, 0.999999f, 1.0f, 1.000001f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f};
	private final int VAO;

	private AABBShader shader;
	private int aabbCount;
	private boolean lastWireframe;

	public AABBRenderer() {
		shader = new AABBShader();
		aabbCount = 0;
		lastWireframe = false;

		VAO = Loader.createVAO();
		Loader.createIndicesVBO(VAO, INDICES);
		Loader.storeDataInVBO(VAO, VERTICES, 0, 3);
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		prepareRendering(clipPlane, camera);

		for (final AABB aabb : AABBManager.getRenderAABB()) {
			renderAABB(aabb);
		}

		endRendering();

		FlounderProfiler.add("AABB", "Render Count", aabbCount);
		FlounderProfiler.add("AABB", "Render Time", super.getRenderTimeMs());
		aabbCount = 0;
	}

	private void prepareRendering(final Vector4f clipPlane, final ICamera camera) {
		shader.start();
		shader.projectionMatrix.loadMat4(FlounderEngine.getProjectionMatrix());
		shader.viewMatrix.loadMat4(camera.getViewMatrix());
		shader.clipPlane.loadVec4(clipPlane);

		lastWireframe = OpenglUtils.isInWireframe();

		OpenglUtils.antialias(FlounderDevices.getDisplay().isAntialiasing());
		OpenglUtils.cullBackFaces(false);
		OpenglUtils.goWireframe(true);
		OpenglUtils.enableDepthTesting();

		OpenglUtils.bindVAO(VAO, 0);
	}

	private void renderAABB(final AABB aabb) {
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
		OpenglUtils.goWireframe(lastWireframe);

		OpenglUtils.unbindVAO(0);
		shader.stop();

		AABBManager.clear();
		aabbCount++;
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}