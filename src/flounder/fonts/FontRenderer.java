package flounder.fonts;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * A renderer capable of rendering fonts.
 */
public class FontRenderer extends IRenderer {
	private FontShader shader;

	private boolean lastWireframe;

	/**
	 * Creates a new font renderer.
	 */
	public FontRenderer() {
		shader = new FontShader();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (FlounderEngine.getFonts().getTexts().keySet().size() < 1) {
			return;
		}

		prepareRendering();
		FlounderEngine.getFonts().getTexts().keySet().forEach(font -> FlounderEngine.getFonts().getTexts().get(font).forEach(this::renderText));
		endRendering();
	}

	@Override
	public void profile() {
		if (FlounderEngine.getProfiler().isOpen()) {
			FlounderEngine.getProfiler().add("Fonts", "Render Time", super.getRenderTimeMs());
		}
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
		OpenGlUtils.bindVAO(text.getMesh(), 0, 1);
		OpenGlUtils.bindTextureToBank(text.getFontType().getTextureAtlas(), 0);
		Vector2f textPosition = text.getPosition();
		Colour textColour = text.getColour();
		((UniformFloat) shader.getUniform("aspectRatio")).loadFloat(FlounderEngine.getDevices().getDisplay().getAspectRatio());
		((UniformVec3) shader.getUniform("transform")).loadVec3(textPosition.x, textPosition.y, text.getScale());
		((UniformVec4) shader.getUniform("colour")).loadVec4(textColour.getR(), textColour.getG(), textColour.getB(), text.getTransparency());
		((UniformVec3) shader.getUniform("borderColour")).loadVec3(text.getBorderColour());
		((UniformVec2) shader.getUniform("edgeData")).loadVec2(text.calculateEdgeStart(), text.calculateAntialiasSize());
		((UniformVec2) shader.getUniform("borderSizes")).loadVec2(text.getTotalBorderSize(), text.getGlowSize());
		glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
		OpenGlUtils.unbindVAO(0, 1);
	}
}
