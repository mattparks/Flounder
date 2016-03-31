package flounder.guis;

import flounder.engine.*;
import flounder.loaders.*;
import flounder.maths.vectors.*;
import org.lwjgl.opengl.*;

public class GuiRenderer extends IRenderer {
	private static final float[] POSITIONS = {0, 0, 0, 1, 1, 0, 1, 1};

	private GuiShader m_shader;
	private int m_vao;

	public GuiRenderer() {
		m_vao = Loader.createInterleavedVAO(POSITIONS, 2);
		m_shader = new GuiShader();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		prepareRendering();

		GuiManager.getGuiTextures().forEach(this::renderGui);

		endRendering();
	}

	private void prepareRendering() {
		OpenglUtils.antialias(false);
		OpenglUtils.cullBackFaces(true);
		OpenglUtils.enableAlphaBlending();
		OpenglUtils.disableDepthTesting();
		m_shader.start();
	}

	private void renderGui(GuiTexture gui) {
		if (!gui.getTexture().isLoaded()) {
			return;
		}

		OpenglUtils.bindVAO(m_vao, 0);
		OpenglUtils.bindTextureToBank(gui.getTexture().getTextureID(), 0);
		m_shader.transform.loadVec4(gui.getPosition().x, gui.getPosition().y, gui.getScale().x, gui.getScale().y);
		m_shader.alpha.loadFloat(gui.getAlpha());
		m_shader.flipTexture.loadBoolean(gui.isFlipTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, POSITIONS.length / 2);
		OpenglUtils.unbindVAO(0);
	}

	private void endRendering() {
		m_shader.stop();
	}

	@Override
	public void dispose() {
		m_shader.dispose();
	}
}
