package flounder.fonts;

import flounder.devices.*;
import flounder.engine.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer extends IRenderer {
	private FontShader shader;

	private int textCount;
	private boolean lastWireframe;

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

		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add("Font", "Render Count", textCount);
			FlounderProfiler.add("Font", "Render Time", super.getRenderTimeMs());
		}

		textCount = 0;
	}

	private void prepareRendering() {
		shader.start();

		lastWireframe = OpenglUtils.isInWireframe();

		OpenglUtils.antialias(false);
		OpenglUtils.enableAlphaBlending();
		OpenglUtils.disableDepthTesting();
		OpenglUtils.cullBackFaces(true);
		OpenglUtils.goWireframe(false);
	}

	private void endRendering() {
		OpenglUtils.goWireframe(lastWireframe);

		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}

	private void renderText(Text text) {
		textCount++;

		OpenglUtils.bindVAO(text.getMesh(), 0, 1);
		OpenglUtils.bindTextureToBank(text.getFontType().getTextureAtlas(), 0);
		Vector2f textPosition = text.getPosition();
		Colour textColour = text.getColour();
		shader.aspectRatio.loadFloat(FlounderDevices.getDisplay().getAspectRatio());
		shader.transform.loadVec3(textPosition.x, textPosition.y, text.getScale());
		shader.colour.loadVec4(textColour.getR(), textColour.getG(), textColour.getB(), text.getTransparency());
		shader.borderColour.loadVec3(text.getBorderColour());
		shader.edgeData.loadVec2(text.calculateEdgeStart(), text.calculateAntialiasSize());
		shader.borderSizes.loadVec2(text.getTotalBorderSize(), text.getGlowSize());
		glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
		OpenglUtils.unbindVAO(0, 1);
	}
}
