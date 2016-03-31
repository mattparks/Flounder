package flounder.fonts;

import flounder.devices.*;
import flounder.engine.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import org.lwjgl.opengl.*;

import java.util.*;

public class FontRenderer extends IRenderer {
	private FontShader fontShader;

	public FontRenderer() {
		fontShader = new FontShader();
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
		fontShader.start();
	}

	private void renderText(final Text text) {
		OpenglUtils.bindVAO(text.getMesh(), 0, 1);
		OpenglUtils.bindTextureToBank(text.getFontType().getTextureAtlas(), 0);
		Vector2f textPosition = text.getPosition();
		Colour textColour = text.getColour();
		fontShader.transform.loadVec3(textPosition.x, textPosition.y, text.getScale());
		fontShader.aspectRatio.loadFloat(ManagerDevices.getDisplay().getDisplayAspectRatio());
		fontShader.colour.loadVec4(textColour.getR(), textColour.getG(), textColour.getB(), text.getTransparency());
		fontShader.borderColour.loadVec3(text.getBorderColour());
		fontShader.borderSizes.loadVec2(new Vector2f(text.getTotalBorderSize(), text.getGlowSize()));
		fontShader.edgeData.loadVec2(new Vector2f(text.calculateEdgeStart(), text.calculateAntialiasSize()));
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		OpenglUtils.unbindVAO(0, 1);
	}

	private void endRendering() {
		fontShader.stop();
	}

	@Override
	public void dispose() {
		fontShader.dispose();
	}
}
