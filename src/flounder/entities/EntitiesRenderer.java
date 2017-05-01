package flounder.entities;

import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import static flounder.platform.Constants.*;

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
		this.shader = ShaderFactory.newBuilder().setName("entities").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
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

		if (FlounderEntities.get().getEntities() != null) {
			for (Entity entity : FlounderEntities.get().getEntities().queryInFrustum(camera.getViewFrustum())) {
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

		OpenGlUtils.antialias(FlounderDisplay.get().isAntialiasing());
		OpenGlUtils.enableAlphaBlending();
		OpenGlUtils.enableDepthTesting();

		renderedCount = 0;
	}

	private void renderEntity(Entity entity) {
		if (entity == null) {
			return;
		}

		OpenGlUtils.bindTexture(textureUndefined, 0);
		Single<Integer> vaoLength = new Single<>(0);

		for (IComponentEntity component : entity.getComponents()) {
			if (component instanceof IComponentRender) {
				((IComponentRender) component).render(shader, vaoLength);
			}
		}

		if (vaoLength.getSingle() > 0) {
			OpenGlUtils.renderElements(GL_TRIANGLES, GL_UNSIGNED_INT, vaoLength.getSingle());
		}

		for (IComponentEntity component : entity.getComponents()) {
			if (component instanceof IComponentRender) {
				((IComponentRender) component).renderClear(shader);
			}
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
		FlounderProfiler.get().add(FlounderEntities.getTab(), "Render Time", super.getRenderTime());
		FlounderProfiler.get().add(FlounderEntities.getTab(), "Rendered Count", renderedCount);
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}