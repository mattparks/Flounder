package flounder.fonts;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * A renderer capable of rendering fonts.
 */
public class FontRenderer extends IRenderer {
	private FontShader shader;

	private int textCount;
	private boolean lastWireframe;

	/**
	 * Creates a new font renderer.
	 */
	public FontRenderer() {
		shader = new FontShader();
		textCount = 0;
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (FontManager.getTexts().keySet().size() < 1) {
			return;
		}

		prepareRendering();
		FontManager.getTexts().keySet().forEach(font -> FontManager.getTexts().get(font).forEach(this::renderText));
		endRendering();

		if (FlounderEngine.getProfiler().isOpen()) {
			FlounderEngine.getProfiler().add("Font", "Render Count", textCount);
			FlounderEngine.getProfiler().add("Font", "Render Time", super.getRenderTimeMs());
		}

		textCount = 0;
	}

	private void prepareRendering() {
		shader.start();

		lastWireframe = OpenGlUtils.isInWireframe();

		OpenGlUtils.antialias(false);
		OpenGlUtils.enableAlphaBlending();
		OpenGlUtils.disableDepthTesting();
		OpenGlUtils.cullBackFaces(true);
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

	private void renderText(Text text) {
		textCount++;

		OpenGlUtils.bindVAO(text.getMesh(), 0, 1);
		OpenGlUtils.bindTextureToBank(text.getFontType().getTextureAtlas(), 0);
		Vector2f textPosition = text.getPosition();
		Colour textColour = text.getColour();
		shader.aspectRatio.loadFloat(FlounderEngine.getDevices().getDisplay().getAspectRatio());
		shader.transform.loadVec3(textPosition.x, textPosition.y, text.getScale());
		shader.colour.loadVec4(textColour.getR(), textColour.getG(), textColour.getB(), text.getTransparency());
		shader.borderColour.loadVec3(text.getBorderColour());
		shader.edgeData.loadVec2(text.calculateEdgeStart(), text.calculateAntialiasSize());
		shader.borderSizes.loadVec2(text.getTotalBorderSize(), text.getGlowSize());
		glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
		OpenGlUtils.unbindVAO(0, 1);
	}
}
