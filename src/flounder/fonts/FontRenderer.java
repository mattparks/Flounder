package flounder.fonts;

import flounder.devices.*;
import flounder.engine.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import org.lwjgl.opengl.*;

import java.util.*;

public class FontRenderer extends IRenderer {
	private FontShader m_fontShader;

	public FontRenderer() {
		m_fontShader = new FontShader();
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		prepareRendering(false);
		Map<FontType, List<Text>> texts = GuiManager.getTexts();

		for (FontType font : texts.keySet()) {
			texts.get(font).forEach(this::renderText);
		}

		endRendering();
	}

	private void prepareRendering(final boolean antiAliasing) {
		OpenglUtils.antialias(antiAliasing);
		OpenglUtils.cullBackFaces(true);
		OpenglUtils.enableAlphaBlending();
		OpenglUtils.disableDepthTesting();
		m_fontShader.start();
	}

	private void renderText(final Text text) {
		OpenglUtils.bindVAO(text.getMesh(), 0, 1);
		OpenglUtils.bindTextureToBank(text.getFontType().getTextureAtlas(), 0);
		Vector2f textPosition = text.getPosition();
		Colour textColour = text.getColour();
		m_fontShader.transform.loadVec3(textPosition.x, textPosition.y, text.getScale());
		m_fontShader.aspectRatio.loadFloat(ManagerDevices.getDisplay().getDisplayAspectRatio());
		m_fontShader.colour.loadVec4(textColour.getR(), textColour.getG(), textColour.getB(), text.getTransparency());
		m_fontShader.borderColour.loadVec3(text.getBorderColour());
		m_fontShader.borderSizes.loadVec2(new Vector2f(text.getTotalBorderSize(), text.getGlowSize()));
		m_fontShader.edgeData.loadVec2(new Vector2f(text.calculateEdgeStart(), text.calculateAntialiasSize()));
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		OpenglUtils.unbindVAO(0, 1);
	}

	private void endRendering() {
		m_fontShader.stop();
	}

	@Override
	public void dispose() {
		m_fontShader.dispose();
	}
}
