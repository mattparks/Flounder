package flounder.physics;

import flounder.devices.*;
import flounder.engine.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.ARBDrawInstanced.glDrawArraysInstancedARB;
import static org.lwjgl.opengl.GL11.*;

public class AABBRenderer extends IRenderer {
	public static final Vector3f ROTATION_REUSABLE = new Vector3f(0, 0, 0);
	public static final Vector3f POSITION_REUSABLE = new Vector3f(0, 0, 0);
	public static final Vector3f SCALE_REUSABLE = new Vector3f(0, 0, 0);
	public static final Matrix4f MODEL_MATRIX_REUSABLE = new Matrix4f();

	private static final int MAX_INSTANCES = 10000;
	private static final int INSTANCE_DATA_LENGTH = 19;

	private final int VAO;
	private final int VAO_LENGTH;
	private final FloatBuffer BUFFER;
	private final int VBO;
	private int pointer;

	private AABBShader shader;
	private boolean lastWireframe;

	public AABBRenderer() {
		shader = new AABBShader();
		lastWireframe = false;

		// Creates the basic cube.
		final float[] verticies = new float[]{ 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f, 0.999999f, 1.0f, 1.000001f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 0.999999f, 1.0f, 1.000001f, 1.0f, -1.0f, 1.0f, 0.999999f, 1.0f, 1.000001f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -0.999999f };
		VAO = Loader.createInterleavedVAO(verticies, 3);
		VAO_LENGTH = verticies.length;

		// Creates the instanced array stuff.
		BUFFER = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
		VBO = Loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		this.pointer = 0;

		Loader.addInstancedAttribute(VAO, VBO, 1, 4, INSTANCE_DATA_LENGTH, 0);  // Model Mat A
		Loader.addInstancedAttribute(VAO, VBO, 2, 4, INSTANCE_DATA_LENGTH, 4);  // Model Mat B
		Loader.addInstancedAttribute(VAO, VBO, 3, 4, INSTANCE_DATA_LENGTH, 8);  // Model Mat C
		Loader.addInstancedAttribute(VAO, VBO, 4, 4, INSTANCE_DATA_LENGTH, 12); // Model Mat D
		Loader.addInstancedAttribute(VAO, VBO, 5, 3, INSTANCE_DATA_LENGTH, 16); // Colours
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		final int instances = AABBManager.getRenderAABB().size();

		if (instances < 1) {
			return;
		}

		pointer = 0;
		final float[] vboData = new float[instances * INSTANCE_DATA_LENGTH];

		for (final AABB aabb : AABBManager.getRenderAABB()) {
			loadAABB(aabb, vboData);
		}

		prepareRendering(clipPlane, camera);

		try {
			Loader.updateVBO(VBO, vboData, BUFFER);
		} catch (final BufferOverflowException e) {
			FlounderLogger.exception(e);
		}

		glDrawArraysInstancedARB(GL_TRIANGLE_STRIP, 0, VAO_LENGTH, instances);
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

		OpenglUtils.bindVAO(VAO, 0, 1, 2, 3, 4, 5);
	}

	private void loadAABB(final AABB aabb, final float[] vboData) {
		Vector3f.add(aabb.getMaxExtents(), aabb.getMinExtents(), POSITION_REUSABLE);
		POSITION_REUSABLE.set(POSITION_REUSABLE.x / 2.0f, POSITION_REUSABLE.y / 2.0f, POSITION_REUSABLE.z / 2.0f);
		ROTATION_REUSABLE.set(0.0f, 0.0f, 0.0f);
		Vector3f.subtract(aabb.getMaxExtents(), aabb.getMinExtents(), SCALE_REUSABLE);
		SCALE_REUSABLE.set(SCALE_REUSABLE.x / 2.0f, SCALE_REUSABLE.y / 2.0f, SCALE_REUSABLE.z / 2.0f);
		MODEL_MATRIX_REUSABLE.setIdentity();
		Matrix4f.transformationMatrix(POSITION_REUSABLE, ROTATION_REUSABLE, SCALE_REUSABLE, MODEL_MATRIX_REUSABLE);

		final Vector3f colour = POSITION_REUSABLE.normalize();

		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m00;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m01;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m02;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m03;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m10;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m11;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m12;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m13;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m20;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m21;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m22;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m23;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m30;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m31;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m32;
		vboData[pointer++] = MODEL_MATRIX_REUSABLE.m33;
		vboData[pointer++] = colour.x;
		vboData[pointer++] = colour.y;
		vboData[pointer++] = colour.z;
	}

	private void endRendering() {
		OpenglUtils.goWireframe(lastWireframe);

		OpenglUtils.unbindVAO(0, 1, 2, 3, 4, 5);
		shader.stop();

		AABBManager.clear();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
