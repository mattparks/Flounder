package flounder.guis;

import flounder.camera.*;
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

	private static final float[] POSITIONS = {0, 0, 0, 1, 1, 0, 1, 1};

	private Shader shader;
	private int vaoID;

	private boolean lastWireframe;

	public GuiRenderer() {
		shader = Shader.newShader("guis").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).createInSecondThread();
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

	@Override
	public void profile() {
		FlounderProfiler.add("GUIs", "Render Time", super.getRenderTimeMs());
	}

	private void prepareRendering() {
		shader.start();

		lastWireframe = OpenGlUtils.isInWireframe();

		OpenGlUtils.antialias(false);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.enableAlphaBlending();
		OpenGlUtils.disableDepthTesting();
		OpenGlUtils.goWireframe(false);
	}

	private void renderGui(GuiTexture gui) {
		if (!gui.getTexture().isLoaded()) {
			return;
		}

		OpenGlUtils.bindVAO(vaoID, 0);
		OpenGlUtils.bindTextureToBank(gui.getTexture().getTextureID(), 0);
		shader.getUniformVec4("transform").loadVec4(gui.getPosition().x, gui.getPosition().y, gui.getScale().x, gui.getScale().y);
		shader.getUniformFloat("alpha").loadFloat(gui.getAlpha());
		shader.getUniformBool("flipTexture").loadBoolean(gui.isFlipTexture());
		shader.getUniformFloat("atlasRows").loadFloat(gui.getTexture().getNumberOfRows());
		shader.getUniformVec2("atlasOffset").loadVec2(gui.getTextureOffset());
		shader.getUniformVec3("colourOffset").loadVec3(gui.getColourOffset());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, POSITIONS.length / 2);
		OpenGlUtils.unbindVAO(0);
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
