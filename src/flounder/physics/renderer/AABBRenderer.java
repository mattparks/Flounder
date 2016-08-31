package flounder.physics.renderer;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * A renderer that is used to render AABB's.
 */
public class AABBRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile(ShaderProgram.SHADERS_LOC, "aabbs", "aabbVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(ShaderProgram.SHADERS_LOC, "aabbs", "aabbFragment.glsl");

	public static Vector3f ROTATION_REUSABLE = new Vector3f(0, 0, 0);
	public static Vector3f POSITION_REUSABLE = new Vector3f(0, 0, 0);
	public static Vector3f SCALE_REUSABLE = new Vector3f(0, 0, 0);
	public static Matrix4f MODEL_MATRIX_REUSABLE = new Matrix4f();

	public static Colour colourRed = new Colour(1, 0, 0, 1);

	private Model aabbModel;

	private ShaderProgram shader;

	private boolean lastWireframe;

	/**
	 * Creates a new AABB renderer.
	 */
	public AABBRenderer() {
		shader = new ShaderProgram("aabbs", VERTEX_SHADER, FRAGMENT_SHADER);
		lastWireframe = false;

		aabbModel = Model.newModel(new MyFile(MyFile.RES_FOLDER, "models", "aabb.obj")).createInBackground();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (!FlounderEngine.getAABBs().renders()) {
			return;
		}

		prepareRendering(clipPlane, camera);

		for (AABB aabb : FlounderEngine.getAABBs().getRenderAABB()) {
			renderAABB(aabb);
		}

		endRendering();
	}

	@Override
	public void profile() {
		if (FlounderEngine.getProfiler().isOpen()) {
			FlounderEngine.getProfiler().add("AABBs", "Render Time", super.getRenderTimeMs());
		}
	}

	private void prepareRendering(Vector4f clipPlane, ICamera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderEngine.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		lastWireframe = OpenGlUtils.isInWireframe();

		OpenGlUtils.antialias(FlounderEngine.getDevices().getDisplay().isAntialiasing());
		OpenGlUtils.cullBackFaces(false);
		OpenGlUtils.goWireframe(true);
		OpenGlUtils.enableDepthTesting();

		OpenGlUtils.bindVAO(aabbModel.getVaoID(), 0, 1, 2, 3);
	}

	private void renderAABB(AABB aabb) {
		Vector3f.add(aabb.getMaxExtents(), aabb.getMinExtents(), POSITION_REUSABLE);
		POSITION_REUSABLE.set(POSITION_REUSABLE.x / 2.0f, POSITION_REUSABLE.y / 2.0f, POSITION_REUSABLE.z / 2.0f);

		ROTATION_REUSABLE.set(aabb.getRotation());

		Vector3f.subtract(aabb.getMaxExtents(), aabb.getMinExtents(), SCALE_REUSABLE);
		SCALE_REUSABLE.set(SCALE_REUSABLE.x / 2.0f, SCALE_REUSABLE.y / 2.0f, SCALE_REUSABLE.z / 2.0f);

		MODEL_MATRIX_REUSABLE.setIdentity();
		Matrix4f.transformationMatrix(POSITION_REUSABLE, ROTATION_REUSABLE, SCALE_REUSABLE, MODEL_MATRIX_REUSABLE);

		shader.getUniformMat4("modelMatrix").loadMat4(MODEL_MATRIX_REUSABLE);
		shader.getUniformVec3("colour").loadVec3(colourRed); // POSITION_REUSABLE.normalize()

		glDrawElements(GL_TRIANGLES, aabbModel.getVaoLength(), GL_UNSIGNED_INT, 0);
	}

	private void endRendering() {
		OpenGlUtils.goWireframe(lastWireframe);
		OpenGlUtils.unbindVAO(0, 1, 2, 3);
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}