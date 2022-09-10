package com.flounder.entities;

import com.flounder.camera.*;
import com.flounder.devices.*;
import com.flounder.helpers.*;
import com.flounder.maths.vectors.*;
import com.flounder.renderer.*;
import com.flounder.resources.*;
import com.flounder.shaders.*;
import com.flounder.textures.*;

import java.util.*;

import static com.flounder.platform.Constants.*;

/**
 * A renderer that is used to render entity's.
 */
public class EntitiesRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "entities", "entityVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "entities", "entityFragment.glsl");

	private ShaderObject shader;
	private TextureObject textureUndefined;
	private List<Entity> objects;

	/**
	 * Creates a new entity renderer.
	 */
	public EntitiesRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("entities").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
		this.textureUndefined = TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "undefined.png")).create();
		this.objects = new ArrayList<>();
	}

	@Override
	public void render(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || camera == null) {
			return;
		}

		prepareRendering(clipPlane, camera);

		if (FlounderEntities.get().getEntities() != null) {
			for (Entity entity : FlounderEntities.get().getEntities().queryInFrustum(camera.getViewFrustum(), objects)) {
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

		FlounderOpenGL.get().antialias(FlounderDisplay.get().isAntialiasing());
		FlounderOpenGL.get().enableAlphaBlending();
		FlounderOpenGL.get().enableDepthTesting();
	}

	private void renderEntity(Entity entity) {
		if (entity == null) {
			return;
		}

		FlounderOpenGL.get().bindTexture(textureUndefined, 0);
		Single<Integer> vaoLength = new Single<>(0);

		for (IComponentEntity component : entity.getComponents()) {
			if (component instanceof IComponentRender) {
				((IComponentRender) component).render(shader, vaoLength);
			}
		}

		if (vaoLength.getSingle() > 0) {
			FlounderOpenGL.get().renderElements(GL_TRIANGLES, GL_UNSIGNED_INT, vaoLength.getSingle());
		}

		for (IComponentEntity component : entity.getComponents()) {
			if (component instanceof IComponentRender) {
				((IComponentRender) component).renderClear(shader);
			}
		}

		FlounderOpenGL.get().unbindVAO(0, 1, 2, 3, 4, 5);
	}

	private void endRendering() {
		shader.stop();
		objects.clear();
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}