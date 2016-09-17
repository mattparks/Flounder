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
import static org.lwjgl.opengl.GL20.*;

/**
 * A renderer that is used to render AABB's.
 */
public class ShapesRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile(Shader.SHADERS_LOC, "shapes", "shapesVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(Shader.SHADERS_LOC, "shapes", "shapesFragment.glsl");

	public static Vector3f ROTATION_REUSABLE = new Vector3f(0, 0, 0);
	public static Matrix4f MODEL_MATRIX_REUSABLE = new Matrix4f();

	public static Colour colourRed = new Colour(1, 0, 0, 1);

	private Shader shader;
	private boolean lastWireframe;

	/**
	 * Creates a new AABB renderer.
	 */
	public ShapesRenderer() {
		shader = Shader.newShader("aabbs").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).createInSecondThread();

		lastWireframe = false;
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (!shader.isLoaded() || !FlounderEngine.getShapes().renders()) {
			return;
		}

		prepareRendering(clipPlane, camera);

		for (Model model : FlounderEngine.getShapes().getRenderShapes().keySet()) {
			prepareModel(model);

			for (IShape shape : FlounderEngine.getShapes().getRenderShapes().get(model)) {
				renderShape(model, shape);
			}

			unbindModel();
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
	}

	private void prepareModel(Model model) {
		OpenGlUtils.bindVAO(model.getVaoID(), 0, 1, 2, 3);
	}

	private void renderShape(Model model, IShape shape) {
		ROTATION_REUSABLE.set(0.0f, 0.0f, 0.0f);

		MODEL_MATRIX_REUSABLE.setIdentity();
		Matrix4f.transformationMatrix(shape.getRenderCentre(), ROTATION_REUSABLE, shape.getRenderScale(), MODEL_MATRIX_REUSABLE);

		shader.getUniformMat4("modelMatrix").loadMat4(MODEL_MATRIX_REUSABLE);
		shader.getUniformVec3("colour").loadVec3(colourRed); // POSITION_REUSABLE.normalize()

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
	public void dispose() {
		shader.dispose();
	}
}