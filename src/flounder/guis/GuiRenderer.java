package flounder.guis;

import flounder.engine.*;
import flounder.loaders.*;
import flounder.maths.vectors.*;
import org.lwjgl.opengl.*;

public class GuiRenderer extends IRenderer {
	private static final float[] POSITIONS = {0, 0, 0, 1, 1, 0, 1, 1};

	private GuiShader shader;
	private int vaoID;

	public GuiRenderer() {
		vaoID = Loader.createInterleavedVAO(POSITIONS, 2);
		shader = new GuiShader();
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		prepareRendering();

		GuiManager.getGuiTextures().forEach(this::renderGui);

		endRendering();
	}

	private void prepareRendering() {
		OpenglUtils.antialias(false);
		OpenglUtils.cullBackFaces(true);
		OpenglUtils.enableAlphaBlending();
		OpenglUtils.disableDepthTesting();
		shader.start();
	}

	private void renderGui(final GuiTexture gui) {
		if (!gui.getTexture().isLoaded()) {
			return;
		}

		OpenglUtils.bindVAO(vaoID, 0);
		OpenglUtils.bindTextureToBank(gui.getTexture().getTextureID(), 0);
		shader.transform.loadVec4(gui.getPosition().x, gui.getPosition().y, gui.getScale().x, gui.getScale().y);
		shader.alpha.loadFloat(gui.getAlpha());
		shader.flipTexture.loadBoolean(gui.isFlipTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, POSITIONS.length / 2);
		OpenglUtils.unbindVAO(0);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
