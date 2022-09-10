package com.flounder.physics.bounding;

import com.flounder.camera.*;
import com.flounder.devices.*;
import com.flounder.maths.*;
import com.flounder.maths.matrices.*;
import com.flounder.maths.vectors.*;
import com.flounder.models.*;
import com.flounder.physics.*;
import com.flounder.renderer.*;
import com.flounder.resources.*;
import com.flounder.shaders.*;

import static com.flounder.platform.Constants.*;

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

	/**
	 * Creates a new Boundings renderer.
	 */
	public BoundingRenderer() {
		shader = ShaderFactory.newBuilder().setName("bounding").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
	}

	@Override
	public void render(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || !FlounderOpenGL.get().isInWireframe() || FlounderBounding.get().getRenderShapes() == null) {
			return;
		}

		prepareRendering(clipPlane, camera);

		for (ModelObject model : FlounderBounding.get().getRenderShapes().keySet()) {
			if (model.isLoaded()) {
				prepareModel(model);

				for (Collider shape : FlounderBounding.get().getRenderShapes().get(model)) {
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

		FlounderOpenGL.get().antialias(FlounderDisplay.get().isAntialiasing());
		FlounderOpenGL.get().cullBackFaces(false);
		FlounderOpenGL.get().goWireframe(true);
		FlounderOpenGL.get().enableDepthTesting();
	}

	private void prepareModel(ModelObject model) {
		FlounderOpenGL.get().bindVAO(model.getVaoID(), 0, 1, 2, 3);
	}

	private void renderShape(ModelObject model, Collider shape) {
		if (model == null || !model.isLoaded()) {
			return;
		}

		POSITION_REUSABLE.set(0.0f, 0.0f, 0.0f);
		ROTATION_REUSABLE.set(0.0f, 0.0f, 0.0f);
		SCALE_REUSABLE.set(0.0f, 0.0f, 0.0f);
		MODEL_MATRIX_REUSABLE.setIdentity();
		COLOUR_REUSABLE.set(0.0f, 0.0f, 0.0f);

		Matrix4f.transformationMatrix(shape.getRenderCentre(POSITION_REUSABLE), shape.getRenderRotation(ROTATION_REUSABLE), shape.getRenderScale(SCALE_REUSABLE), MODEL_MATRIX_REUSABLE);

		shader.getUniformMat4("modelMatrix").loadMat4(MODEL_MATRIX_REUSABLE);
		shader.getUniformVec3("colour").loadVec3(shape.getRenderColour(COLOUR_REUSABLE));

		FlounderOpenGL.get().renderElements(GL_TRIANGLES, GL_UNSIGNED_INT, model.getVaoLength());
	}

	private void unbindModel() {
		FlounderOpenGL.get().unbindVAO(0, 1, 2, 3);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}