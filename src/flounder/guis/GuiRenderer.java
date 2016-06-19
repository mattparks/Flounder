package flounder.guis;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;

import static org.lwjgl.opengl.GL11.*;

public class GuiRenderer extends IRenderer {
	private static final float[] POSITIONS = {0, 0, 0, 1, 1, 0, 1, 1};

	private GuiShader shader;
	private int vaoID;

	private boolean lastWireframe;

	public GuiRenderer() {
		shader = new GuiShader();
		vaoID = FlounderEngine.getLoader().createInterleavedVAO(POSITIONS, 2);
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (FlounderEngine.getGuis().getGuiTextures().size() < 1) {
			return;
		}

		prepareRendering();
		FlounderEngine.getGuis().getGuiTextures().forEach(this::renderGui);
		endRendering();
	}

	@Override
	public void profile() {
		if (FlounderEngine.getProfiler().isOpen()) {
			FlounderEngine.getProfiler().add("GUIs", "Render Time", super.getRenderTimeMs());
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
		shader.transform.loadVec4(gui.getPosition().x, gui.getPosition().y, gui.getScale().x, gui.getScale().y);
		shader.alpha.loadFloat(gui.getAlpha());
		shader.flipTexture.loadBoolean(gui.isFlipTexture());
		shader.atlasRows.loadFloat(gui.getTexture().getNumberOfRows());
		shader.atlasOffset.loadVec2(gui.getTextureOffset());
		shader.colourOffset.loadVec3(gui.getColourOffset());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, POSITIONS.length / 2);
		OpenGlUtils.unbindVAO(0);
	}
}
