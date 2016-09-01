package flounder.guis;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;

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
	private GuiRenderType type;

	public GuiRenderer(GuiRenderType type) {
		shader = Shader.newShader("guis").setVertex(VERTEX_SHADER).setFragment(FRAGMENT_SHADER).createInSecondThread();
		vaoID = FlounderEngine.getLoader().createInterleavedVAO(POSITIONS, 2);
		this.type = type;
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		switch (type) {
			case GUI:
				if (!shader.isLoaded() || FlounderEngine.getGuis().getGuiTextures().size() < 1) {
					return;
				}

				prepareRendering();
				FlounderEngine.getGuis().getGuiTextures().forEach(this::renderGui);
				endRendering();
				break;
			case CURSOR:
				if (!shader.isLoaded() || !FlounderEngine.getCursor().isShown()) {
					return;
				}

				prepareRendering();
				renderGui(FlounderEngine.getCursor().getCursorTexture());
				endRendering();
				break;
		}
	}

	@Override
	public void profile() {
		switch (type) {
			case GUI:
				FlounderEngine.getProfiler().add("GUIs", "Render Time", super.getRenderTimeMs());
				break;
			case CURSOR:
				FlounderEngine.getProfiler().add("Cursor", "Render Time", super.getRenderTimeMs());
				break;
		}
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

	private void endRendering() {
		OpenGlUtils.goWireframe(lastWireframe);

		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
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
}
