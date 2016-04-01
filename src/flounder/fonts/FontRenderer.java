package flounder.fonts;

import flounder.devices.*;
import flounder.engine.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import org.lwjgl.opengl.*;

public class FontRenderer extends IRenderer {
	private FontShader fontShader;

	public FontRenderer() {
		fontShader = new FontShader();
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		prepareRendering();
		//	GuiManager.getTexts().keySet().forEach(font -> GuiManager.getTexts().get(font).forEach(this::renderText));
		endRendering();
	}

	private void prepareRendering() {
		OpenglUtils.antialias(false);
		OpenglUtils.enableAlphaBlending();
		OpenglUtils.disableDepthTesting();
		OpenglUtils.cullBackFaces(true);
		fontShader.start();
	}

	private void endRendering() {
		fontShader.stop();
	}

	@Override
	public void dispose() {
		fontShader.dispose();
	}

	private void renderText(final Text text) {
		OpenglUtils.bindVAO(text.getMesh(), 0, 1);
		OpenglUtils.bindTextureToBank(text.getFontType().getTextureAtlas(), 0);
		Vector2f textPosition = text.getPosition();
		Colour textColour = text.getColour();
		fontShader.aspectRatio.loadFloat(ManagerDevices.getDisplay().getAspectRatio());
		fontShader.transform.loadVec3(textPosition.x, textPosition.y, text.getScale());
		fontShader.colour.loadVec4(textColour.getR(), textColour.getG(), textColour.getB(), text.getTransparency());
		fontShader.borderColour.loadVec3(text.getBorderColour());
		fontShader.edgeData.loadVec2(text.calculateEdgeStart(), text.calculateAntialiasSize());
		fontShader.borderSizes.loadVec2(text.getTotalBorderSize(), text.getGlowSize());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		OpenglUtils.unbindVAO(0, 1);
	}
}
