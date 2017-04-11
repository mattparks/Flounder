package flounder.entities;

import flounder.camera.*;
import flounder.devices.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;
import org.lwjgl.opengl.*;

/**
 * A renderer that is used to render entity's.
 */
public class EntitiesRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "entities", "entityVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "entities", "entityFragment.glsl");

	private ShaderObject shader;
	private TextureObject textureUndefined;
	private int renderedCount;
	private boolean renderPlayer;

	/**
	 * Creates a new entity renderer.
	 */
	public EntitiesRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("entities").addType(new ShaderType(GL20.GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
		this.textureUndefined = TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "undefined.png")).create();
		this.renderedCount = 0;
		this.renderPlayer = true;
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || camera == null) {
			return;
		}

		prepareRendering(clipPlane, camera);

		if (FlounderEntities.getEntities() != null) {
			for (Entity entity : FlounderEntities.getEntities().queryInFrustum(FlounderCamera.getCamera().getViewFrustum())) {
				renderEntity(entity);
			}
		}

		endRendering();
	}

	private void prepareRendering(Vector4f clipPlane, Camera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.enableAlphaBlending();

		renderedCount = 0;
	}

	private void renderEntity(Entity entity) {
		if (entity == null) {
			return;
		}

		if (!renderPlayer && entity.getComponent(ComponentPlayer.class) != null) {
			return;
		}

		ComponentModel componentModel = (ComponentModel) entity.getComponent(ComponentModel.class);
		ComponentAnimation componentAnimation = (ComponentAnimation) entity.getComponent(ComponentAnimation.class);
		ComponentSurface componentSurface = (ComponentSurface) entity.getComponent(ComponentSurface.class);
		ComponentGlow componentGlow = (ComponentGlow) entity.getComponent(ComponentGlow.class);
		ComponentSway componentSway = (ComponentSway) entity.getComponent(ComponentSway.class);
		final int vaoLength;

		if (componentModel != null && componentModel.getModel() != null && componentModel.getModel().isLoaded()) {
			OpenGlUtils.bindVAO(componentModel.getModel().getVaoID(), 0, 1, 2, 3);
			shader.getUniformBool("animated").loadBoolean(false);

			if (componentModel.getModelMatrix() != null) {
				shader.getUniformMat4("modelMatrix").loadMat4(componentModel.getModelMatrix());
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
			OpenGlUtils.bindVAO(componentAnimation.getModel().getVaoID(), 0, 1, 2, 3, 4, 5);
			shader.getUniformBool("animated").loadBoolean(true);

			if (componentAnimation.getModelMatrix() != null) {
				shader.getUniformMat4("modelMatrix").loadMat4(componentAnimation.getModelMatrix());
			}

			// Just stop if you are trying to apply a sway to a animated object, rethink life.
			shader.getUniformFloat("swayHeight").loadFloat(0.0f);
			vaoLength = componentAnimation.getModel().getVaoLength();

			// Loads joint transforms.
			Matrix4f[] jointMatrices = componentAnimation.getJointTransforms();

			for (int i = 0; i < jointMatrices.length; i++) {
				shader.getUniformMat4("jointTransforms[" + i + "]").loadMat4(jointMatrices[i]);
			}
		} else {
			// No model, so no render!
			return;
		}

		if (componentModel != null && componentModel.getTexture() != null && componentModel.getTexture().isLoaded()) {
			shader.getUniformFloat("atlasRows").loadFloat(componentModel.getTexture().getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(componentModel.getTextureOffset());
			shader.getUniformVec3("colourOffset").loadVec3(componentModel.getColourOffset());
			OpenGlUtils.cullBackFaces(!componentModel.getTexture().hasAlpha());
			OpenGlUtils.bindTexture(componentModel.getTexture(), 0);
		} else if (componentAnimation != null && componentAnimation.getTexture() != null && componentAnimation.getTexture().isLoaded()) {
			shader.getUniformFloat("atlasRows").loadFloat(componentAnimation.getTexture().getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(componentAnimation.getTextureOffset());
			shader.getUniformVec3("colourOffset").loadVec3(componentAnimation.getColourOffset());
			OpenGlUtils.cullBackFaces(!componentAnimation.getTexture().hasAlpha());
			OpenGlUtils.bindTexture(componentAnimation.getTexture(), 0);
		} else if (textureUndefined != null && textureUndefined.isLoaded()) {
			// No texture, so load a 'undefined' texture.
			shader.getUniformFloat("atlasRows").loadFloat(textureUndefined.getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(0, 0);
			shader.getUniformVec3("colourOffset").loadVec3(0.0f, 0.0f, 0.0f);
			OpenGlUtils.cullBackFaces(!textureUndefined.hasAlpha());
			OpenGlUtils.bindTexture(textureUndefined, 0);
		}

		if (componentSurface != null) {
			shader.getUniformFloat("shineDamper").loadFloat(componentSurface.getShineDamper());
			shader.getUniformFloat("reflectivity").loadFloat(componentSurface.getReflectivity());

			shader.getUniformBool("ignoreFog").loadBoolean(componentSurface.isIgnoreFog());
			shader.getUniformBool("ignoreLighting").loadBoolean(componentSurface.isIgnoreLighting());
		} else {
			shader.getUniformFloat("shineDamper").loadFloat(1.0f);
			shader.getUniformFloat("reflectivity").loadFloat(0.0f);

			shader.getUniformBool("ignoreFog").loadBoolean(false);
			shader.getUniformBool("ignoreLighting").loadBoolean(false);
		}

		if (componentGlow != null) {
			shader.getUniformBool("useGlowMap").loadBoolean(true);

			if (componentGlow.getTextureGlow() != null && componentGlow.getTextureGlow().isLoaded()) {
				OpenGlUtils.bindTexture(componentGlow.getTextureGlow(), 1);
			}
		} else {
			shader.getUniformBool("useGlowMap").loadBoolean(false);
		}

		if (componentSway != null) {
			shader.getUniformBool("swaying").loadBoolean(true);
			shader.getUniformVec2("swayOffset").loadVec2(componentSway.getSwayOffsetX(), componentSway.getSwayOffsetZ());

			if (componentSway.getTextureSway() != null && componentSway.getTextureSway().isLoaded()) {
				OpenGlUtils.bindTexture(componentSway.getTextureSway(), 2);
			}
		} else {
			shader.getUniformBool("swaying").loadBoolean(false);
		}

		if (vaoLength > 0) {
			OpenGlUtils.renderElements(GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_INT, vaoLength);
		}

		OpenGlUtils.unbindVAO(0, 1, 2, 3, 4, 5);
		renderedCount++;
	}

	private void endRendering() {
		shader.stop();
	}

	public void setRenderPlayer(boolean renderPlayer) {
		this.renderPlayer = renderPlayer;
	}

	@Override
	public void profile() {
		FlounderProfiler.add(FlounderEntities.PROFILE_TAB_NAME, "Render Time", super.getRenderTime());
		FlounderProfiler.add(FlounderEntities.PROFILE_TAB_NAME, "Rendered Count", renderedCount);
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}