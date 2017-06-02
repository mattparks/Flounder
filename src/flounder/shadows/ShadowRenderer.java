package flounder.shadows;

import flounder.camera.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.fbos.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import java.util.*;

import static flounder.platform.Constants.*;

public class ShadowRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "shadows", "shadowVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "shadows", "shadowFragment.glsl");

	private FBO shadowFBO;
	private ShaderObject shader;

	private Matrix4f mvpReusableMatrix;
	private List<Entity> objects;

	/**
	 * Creates a new entity renderer.
	 */
	public ShadowRenderer() {
		this.shadowFBO = FBO.newFBO(FlounderShadows.get().getShadowSize(), FlounderShadows.get().getShadowSize()).noColourBuffer().disableTextureWrap().depthBuffer(DepthBufferType.TEXTURE).create();
		this.shader = ShaderFactory.newBuilder().setName("shadows").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();

		this.mvpReusableMatrix = new Matrix4f();
		this.objects = new ArrayList<>();
	}

	@Override
	public void render(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || camera == null || FlounderShadows.get().getShadowDarkness() < 0.07f) {
			return;
		}

		if (FlounderShadows.get().renderNow()) {
			prepareRendering(clipPlane, camera);

			if (FlounderEntities.get().getEntities() != null) {
				for (Entity entity : FlounderEntities.get().getEntities().queryInBounding(FlounderShadows.get().getShadowAABB(), objects)) {
					renderEntity(entity);
				}
			}

			endRendering();
		}
	}

	private void prepareRendering(Vector4f clipPlane, Camera camera) {
		if (shadowFBO.getWidth() != FlounderShadows.get().getShadowSize() || shadowFBO.getHeight() != FlounderShadows.get().getShadowSize()) {
			shadowFBO.setSize(FlounderShadows.get().getShadowSize(), FlounderShadows.get().getShadowSize());
		}

		shadowFBO.bindFrameBuffer();
		shader.start();

		FlounderOpenGL.get().prepareNewRenderParse(0.0f, 0.0f, 0.0f);
		FlounderOpenGL.get().antialias(FlounderDisplay.get().isAntialiasing());
		FlounderOpenGL.get().cullBackFaces(false);
		FlounderOpenGL.get().enableDepthTesting();
	}

	private void renderEntity(Entity entity) {
		if (entity == null) {
			return;
		}

		// TODO: Update this cancer.

		ComponentModel componentModel = (ComponentModel) entity.getComponent(ComponentModel.class);
		ComponentAnimation componentAnimation = (ComponentAnimation) entity.getComponent(ComponentAnimation.class);
		ComponentSway componentSway = (ComponentSway) entity.getComponent(ComponentSway.class);
		ComponentSurface componentSurface = (ComponentSurface) entity.getComponent(ComponentSurface.class);

		if (componentSurface != null && !componentSurface.isProjectsShadow()) {
			return;
		}

		final int vaoLength;

		if (componentModel != null && componentModel.getModel() != null && componentModel.getModel().isLoaded()) {
			FlounderOpenGL.get().bindVAO(componentModel.getModel().getVaoID(), 0);
			shader.getUniformBool("animated").loadBoolean(false);

			if (componentModel.getModelMatrix() != null) {
				Matrix4f.multiply(FlounderShadows.get().getProjectionViewMatrix(), componentModel.getModelMatrix(), mvpReusableMatrix);
				shader.getUniformMat4("mvpMatrix").loadMat4(mvpReusableMatrix);
			}

			if (componentModel.getModel().getCollider() != null) {
				float height = 0.0f;

				if (componentModel.getModel().getCollider() instanceof AABB) {
					height = ((AABB) componentModel.getModel().getCollider()).getHeight();
				} else if (componentModel.getModel().getCollider() instanceof Sphere) {
					height = 2.0f * ((Sphere) componentModel.getModel().getCollider()).getRadius();
				}

				shader.getUniformFloat("swayHeight").loadFloat(height);
			}

			vaoLength = componentModel.getModel().getVaoLength();
		} else if (componentAnimation != null && componentAnimation.getModel() != null && componentAnimation.getModel().isLoaded()) {
			FlounderOpenGL.get().bindVAO(componentAnimation.getModel().getVaoID(), 0, 4, 5);
			shader.getUniformBool("animated").loadBoolean(true);

			if (componentAnimation.getModelMatrix() != null) {
				Matrix4f.multiply(FlounderShadows.get().getProjectionViewMatrix(), componentAnimation.getModelMatrix(), mvpReusableMatrix);
				shader.getUniformMat4("mvpMatrix").loadMat4(mvpReusableMatrix);
			}

			// Just stop if you are trying to apply a sway to a animated object, rethink life.
			shader.getUniformFloat("swayHeight").loadFloat(0.0f);
			vaoLength = componentAnimation.getModel().getVaoLength();

			// Loads joint transforms.
			Matrix4f[] jointMatrices = componentAnimation.getJointTransforms();

			if (jointMatrices != null) {
				for (int i = 0; i < jointMatrices.length; i++) {
					shader.getUniformMat4("jointTransforms[" + i + "]").loadMat4(jointMatrices[i]);
				}
			}
		} else {
			// No model, so no render!
			return;
		}

		if (componentSway != null) {
			shader.getUniformBool("swaying").loadBoolean(true);
			shader.getUniformVec2("swayOffset").loadVec2(componentSway.getSwayOffsetX(), componentSway.getSwayOffsetZ());

			if (componentSway.getTextureSway() != null && componentSway.getTextureSway().isLoaded()) {
				FlounderOpenGL.get().bindTexture(componentSway.getTextureSway(), 1);
			}
		} else {
			shader.getUniformBool("swaying").loadBoolean(false);
		}

		if (vaoLength > 0) {
			FlounderOpenGL.get().renderElements(GL_TRIANGLES, GL_UNSIGNED_INT, vaoLength);
		}

		FlounderOpenGL.get().unbindVAO(0, 4, 5);
	}

	private void endRendering() {
		shader.stop();
		shadowFBO.unbindFrameBuffer();
		objects.clear();
	}

	/**
	 * @return The ID of the shadow map texture.
	 */
	public int getShadowMap() {
		return shadowFBO.getDepthTexture();
	}

	@Override
	public void dispose() {
		shader.delete();
		shadowFBO.delete();
	}
}
