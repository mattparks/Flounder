package flounder.skybox;

import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import org.lwjgl.opengl.*;

public class SkyboxRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "skybox", "skyboxVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "skybox", "skyboxFragment.glsl");

	private static final float SIZE = 250.0f;
	private static final float[] VERTICES = {-SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE};

	private ShaderObject shader;
	private int vao;

	public SkyboxRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("skybox").addType(new ShaderType(GL20.GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
		this.vao = FlounderLoader.createVAO();
		FlounderLoader.storeDataInVBO(vao, VERTICES, 0, 3);
		OpenGlUtils.unbindVAO();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || vao == -1) {
			return;
		}

		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformMat4("modelMatrix").loadMat4(FlounderSkybox.getModelMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);
		shader.getUniformBool("polygonMode").loadBoolean(OpenGlUtils.isInWireframe());
		shader.getUniformVec3("skyColour").loadVec3(FlounderSkybox.getFog().getFogColour());
		shader.getUniformFloat("blendFactor").loadFloat(FlounderSkybox.getBlendFactor());

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.disableBlending();

		OpenGlUtils.bindVAO(vao, 0);
		OpenGlUtils.bindTexture(FlounderSkybox.getCubemap(), 0);

		OpenGlUtils.renderArrays(GL11.GL_TRIANGLES, VERTICES.length);

		OpenGlUtils.unbindVAO(0);
		shader.stop();
	}

	@Override
	public void profile() {
		FlounderProfiler.add(FlounderSkybox.PROFILE_TAB_NAME, "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}
