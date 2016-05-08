package flounder.physics;

import flounder.devices.*;
import flounder.engine.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

import static org.lwjgl.opengl.GL11.*;

public class AABBRenderer extends IRenderer {
	public static final Vector3f ROTATION_REUSABLE = new Vector3f(0, 0, 0);
	public static final Vector3f POSITION_REUSABLE = new Vector3f(0, 0, 0);
	public static final Vector3f SCALE_REUSABLE = new Vector3f(0, 0, 0);
	public static final Matrix4f MODEL_MATRIX_REUSABLE = new Matrix4f();

	// private final int[] INDICES = {1, 2, 3, 7, 6, 5, 4, 8, 9, 10, 11, 12, 13, 14, 15, 0, 16, 17, 18, 1, 3, 19, 7, 5, 20, 4, 9, 21, 10, 12, 22, 13, 15, 23, 0, 17};
	private final float[] VERTICES = {1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f, 0.999999f, 1.0f, 1.000001f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 0.999999f, 1.0f, 1.000001f, 1.0f, -1.0f, 1.0f, 0.999999f, 1.0f, 1.000001f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f};
	// private final float[] TEXTURE_COORDS = {0.666467f, 0.666866f, 0.333134f, 1.9997358E-4f, 0.333134f, 0.33313298f, 2.0E-4f, 0.33313298f, 2.0E-4f, 0.666467f, 0.333134f, 0.666866f, 0.333134f, 0.9998f, 2.0E-4f, 0.9998f, 0.333134f, 0.666467f, 0.333134f, 0.333533f, 0.333533f, 0.666467f, 0.666467f, 0.666467f, 0.666467f, 0.333533f, 0.9998f, 0.9998f, 0.666866f, 0.9998f, 0.666866f, 0.666867f, 0.666467f, 0.9998f, 0.333533f, 0.9998f, 2.0E-4f, 1.9997358E-4f, 2.0E-4f, 0.666866f, 2.0E-4f, 0.333533f, 0.333533f, 0.333533f, 0.9998f, 0.666866f, 0.333533f, 0.666866f};
	// private final float[] NORMALS = {0.5773f, -0.5773f, -0.5773f, 0.5773f, -0.5773f, 0.5773f, -0.5773f, -0.5773f, 0.5773f, -0.5773f, -0.5773f, -0.5773f, 0.5773f, 0.5773f, -0.5773f, 0.5773f, 0.5773f, 0.5773f, -0.5773f, 0.5773f, 0.5773f, -0.5773f, 0.5773f, -0.5773f, 0.5773f, 0.5773f, 0.5773f, 0.5773f, -0.5773f, 0.5773f, 0.5773f, 0.5773f, 0.5773f, -0.5773f, 0.5773f, 0.5773f, -0.5773f, -0.5773f, 0.5773f, -0.5773f, -0.5773f, 0.5773f, -0.5773f, 0.5773f, 0.5773f, -0.5773f, 0.5773f, -0.5773f, -0.5773f, -0.5773f, -0.5773f, -0.5773f, 0.5773f, -0.5773f, 0.5773f, -0.5773f, -0.5773f, 0.5773f, 0.5773f, -0.5773f, 0.5773f, -0.5773f, -0.5773f, 0.5773f, -0.5773f, 0.5773f, -0.5773f, -0.5773f, -0.5773f, 0.5773f, 0.5773f, -0.5773f};
	// private final float[] TANGENTS = {0.0f, -1.0f, -2.5331974E-7f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -2.5331974E-7f, 0.0f, 1.0f, -2.5331974E-7f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -2.5331974E-7f, 0.0f, 1.0f, -5.066395E-7f, 0.0f, 1.0f, -2.5331974E-7f, 0.0f, 1.0f, -1.0f, 0.0f, -2.3841864E-7f, -1.0f, 0.0f, -4.768374E-7f, -1.0f, 0.0f, -2.3841864E-7f, 0.0f, -1.0f, 1.4769845E-6f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.4769845E-6f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, -2.5331974E-7f, 0.0f, 0.0f, 1.0f, -5.066395E-7f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 2.953969E-6f, 0.0f, -1.0f, -5.066395E-7f};
	private final int VAO;

	private AABBShader shader;
	private boolean lastWireframe;

	public AABBRenderer() {
		shader = new AABBShader();
		lastWireframe = false;

		VAO = Loader.createInterleavedVAO(VERTICES, 3);
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		prepareRendering(clipPlane, camera);

		for (final AABB aabb : AABBManager.getRenderAABB()) {
			renderAABB(aabb);
		}

		endRendering();
	}

	private void prepareRendering(final Vector4f clipPlane, final ICamera camera) {
		shader.start();
		shader.projectionMatrix.loadMat4(FlounderEngine.getProjectionMatrix());
		shader.viewMatrix.loadMat4(camera.getViewMatrix());
		shader.clipPlane.loadVec4(clipPlane);

		lastWireframe = OpenglUtils.isInWireframe();

		OpenglUtils.antialias(ManagerDevices.getDisplay().isAntialiasing());
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

		glDrawArrays(GL_TRIANGLE_STRIP, 0, VERTICES.length / 3);
	}

	private void endRendering() {
		OpenglUtils.goWireframe(lastWireframe);

		OpenglUtils.unbindVAO(0);
		shader.stop();

		AABBManager.clear();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
