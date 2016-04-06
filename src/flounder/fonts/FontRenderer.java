package flounder.fonts;

import flounder.devices.*;
import flounder.engine.*;
import flounder.engine.profiling.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import org.lwjgl.opengl.*;

public class FontRenderer extends IRenderer {
	private FontShader shader;

	private int textCount;

	public FontRenderer() {
		shader = new FontShader();

		textCount = 0;
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		prepareRendering();
		FontManager.getTexts().keySet().forEach(font -> FontManager.getTexts().get(font).forEach(this::renderText));
		endRendering();

		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add("Font", "Text Count", textCount);
			FlounderProfiler.add("Font", "Render Time", super.getRenderTimeMs());
		}

		textCount = 0;
	}

	private void prepareRendering() {
		OpenglUtils.antialias(false);
		OpenglUtils.enableAlphaBlending();
		OpenglUtils.disableDepthTesting();
		OpenglUtils.cullBackFaces(true);
		shader.start();
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}

	private void renderText(final Text text) {
		textCount++;

		OpenglUtils.bindVAO(text.getMesh(), 0, 1);
		OpenglUtils.bindTextureToBank(text.getFontType().getTextureAtlas(), 0);
		Vector2f textPosition = text.getPosition();
		Colour textColour = text.getColour();
		shader.aspectRatio.loadFloat(ManagerDevices.getDisplay().getAspectRatio());
		shader.transform.loadVec3(textPosition.x, textPosition.y, text.getScale());
		shader.colour.loadVec4(textColour.getR(), textColour.getG(), textColour.getB(), text.getTransparency());
		shader.borderColour.loadVec3(text.getBorderColour());
		shader.edgeData.loadVec2(text.calculateEdgeStart(), text.calculateAntialiasSize());
		shader.borderSizes.loadVec2(text.getTotalBorderSize(), text.getGlowSize());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		OpenglUtils.unbindVAO(0, 1);
	}
}
