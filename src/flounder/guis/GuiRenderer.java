package flounder.guis;

import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GuiRenderer extends IRenderer {
	public enum GuiRenderType {
		GUI, CURSOR
	}

	private static final MyFile VERTEX_SHADER = new MyFile(Shader.SHADERS_LOC, "guis", "guiVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(Shader.SHADERS_LOC, "guis", "guiFragment.glsl");

	private static final float POSITION_MIN = 0.0f;
	private static final float POSITION_MAX = 1.0f;
	private static final float[] POSITIONS = {POSITION_MIN, POSITION_MIN, POSITION_MIN, POSITION_MAX, POSITION_MAX, POSITION_MIN, POSITION_MAX, POSITION_MAX};

	private Shader shader;
	private int vaoID;

	public GuiRenderer() {
		shader = Shader.newShader("guis").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).create();
		vaoID = FlounderLoader.createInterleavedVAO(POSITIONS, 2);
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (!shader.isLoaded() || FlounderGuis.getGuiTextures().isEmpty()) {
			return;
		}

		prepareRendering();
		FlounderGuis.getGuiTextures().forEach(this::renderGui);
		endRendering();
	}

	private void prepareRendering() {
		shader.start();

		OpenGlUtils.antialias(false);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.enableAlphaBlending();
		OpenGlUtils.disableDepthTesting();

		shader.getUniformFloat("aspectRatio").loadFloat(FlounderDisplay.getAspectRatio());
		shader.getUniformBool("polygonMode").loadBoolean(OpenGlUtils.isInWireframe());
	}

	private void renderGui(GuiTexture gui) {
		if (!gui.getTexture().isLoaded()) {
			return;
		}

		OpenGlUtils.bindVAO(vaoID, 0);
		OpenGlUtils.bindTexture(gui.getTexture(), 0);
		shader.getUniformVec2("size").loadVec2((POSITION_MAX - POSITION_MIN) / 2.0f, (POSITION_MAX - POSITION_MIN) / 2.0f);
		shader.getUniformVec4("transform").loadVec4(gui.getPosition().x, gui.getPosition().y, gui.getScale().x, gui.getScale().y);
		shader.getUniformFloat("rotation").loadFloat((float) Math.toRadians(gui.getRotation()));
		shader.getUniformFloat("alpha").loadFloat(gui.getAlpha());
		shader.getUniformBool("flipTexture").loadBoolean(gui.isFlipTexture());
		shader.getUniformFloat("atlasRows").loadFloat(gui.getTexture().getNumberOfRows());
		shader.getUniformVec2("atlasOffset").loadVec2(gui.getTextureOffset());
		shader.getUniformVec3("colourOffset").loadVec3(gui.getColourOffset());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, POSITIONS.length / 2);
		OpenGlUtils.unbindVAO(0);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void profile() {
		FlounderProfiler.add("GUIs", "Render Time", super.getRenderTimeMs());
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
