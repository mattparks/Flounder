package flounder.skybox;

import flounder.camera.*;
import flounder.devices.*;
import flounder.maths.vectors.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import static flounder.platform.Constants.*;

public class SkyboxRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "skybox", "skyboxVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "skybox", "skyboxFragment.glsl");

	private ShaderObject shader;

	public SkyboxRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("skybox").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
	}

	@Override
	public void render(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || !FlounderSkybox.get().getModel().isLoaded()) {
			return;
		}

		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformMat4("modelMatrix").loadMat4(FlounderSkybox.get().getModelMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);
		shader.getUniformBool("polygonMode").loadBoolean(FlounderOpenGL.get().isInWireframe());
		shader.getUniformVec3("skyColour").loadVec3(FlounderSkybox.get().getFog().getFogColour());
		shader.getUniformFloat("blendFactor").loadFloat(FlounderSkybox.get().getBlendFactor());

		FlounderOpenGL.get().antialias(FlounderDisplay.get().isAntialiasing());
		FlounderOpenGL.get().enableDepthTesting();
		FlounderOpenGL.get().depthMask(false);
		FlounderOpenGL.get().cullBackFaces(false);
		FlounderOpenGL.get().disableBlending();

		FlounderOpenGL.get().bindVAO(FlounderSkybox.get().getModel().getVaoID(), 0);
		FlounderOpenGL.get().bindTexture(FlounderSkybox.get().getCubemap(), 0);

		FlounderOpenGL.get().renderElements(GL_TRIANGLES, GL_UNSIGNED_INT, FlounderSkybox.get().getModel().getVaoLength());

		FlounderOpenGL.get().unbindVAO(0);
		FlounderOpenGL.get().depthMask(true);
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}
