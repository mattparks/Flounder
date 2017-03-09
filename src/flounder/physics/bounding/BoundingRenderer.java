package flounder.physics.bounding;

import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * A renderer that is used to render Boundings.
 */
public class BoundingRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "bounding", "boundingVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "bounding", "boundingFragment.glsl");

	private static Vector3f POSITION_REUSABLE = new Vector3f();
	private static Vector3f ROTATION_REUSABLE = new Vector3f();
	private static Vector3f SCALE_REUSABLE = new Vector3f();
	private static Matrix4f MODEL_MATRIX_REUSABLE = new Matrix4f();
	private static Colour COLOUR_REUSABLE = new Colour();

	private ShaderObject shader;
	private boolean lastWireframe;

	/**
	 * Creates a new Boundings renderer.
	 */
	public BoundingRenderer() {
		shader = ShaderFactory.newBuilder().setName("bounding").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
		lastWireframe = false;
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || !FlounderBounding.renders() || FlounderBounding.getRenderShapes() == null) {
			return;
		}

		prepareRendering(clipPlane, camera);

		for (ModelObject model : FlounderBounding.getRenderShapes().keySet()) {
			if (model.isLoaded() && model.getVaoID() != -1 && model.getVaoLength() != -1) {
				prepareModel(model);

				for (IBounding shape : FlounderBounding.getRenderShapes().get(model)) {
					renderShape(model, shape);
				}

				unbindModel();
			}
		}

		endRendering();
	}

	private void prepareRendering(Vector4f clipPlane, Camera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		lastWireframe = OpenGlUtils.isInWireframe();

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.cullBackFaces(false);
		OpenGlUtils.goWireframe(true);
		OpenGlUtils.enableDepthTesting();
	}

	private void prepareModel(ModelObject model) {
		OpenGlUtils.bindVAO(model.getVaoID(), 0, 1, 2, 3);
	}

	private void renderShape(ModelObject model, IBounding shape) {
		if (model == null || !model.isLoaded()) {
			return;
		}

		POSITION_REUSABLE.set(0.0f, 0.0f, 0.0f);
		ROTATION_REUSABLE.set(0.0f, 0.0f, 0.0f);
		SCALE_REUSABLE.set(0.0f, 0.0f, 0.0f);
		MODEL_MATRIX_REUSABLE.setIdentity();
		COLOUR_REUSABLE.set(0.0f, 0.0f, 0.0f);

		Matrix4f.transformationMatrix(shape.getRenderCentre(POSITION_REUSABLE), ROTATION_REUSABLE, shape.getRenderScale(SCALE_REUSABLE), MODEL_MATRIX_REUSABLE);

		shader.getUniformMat4("modelMatrix").loadMat4(MODEL_MATRIX_REUSABLE);
		shader.getUniformVec3("colour").loadVec3(shape.getRenderColour(COLOUR_REUSABLE));

		glDrawElements(GL_TRIANGLES, model.getVaoLength(), GL_UNSIGNED_INT, 0);
	}

	private void unbindModel() {
		OpenGlUtils.unbindVAO(0, 1, 2, 3);
	}

	private void endRendering() {
		OpenGlUtils.goWireframe(lastWireframe);
		shader.stop();
	}

	@Override
	public void profile() {
		FlounderProfiler.add(FlounderBounding.PROFILE_TAB_NAME, "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}