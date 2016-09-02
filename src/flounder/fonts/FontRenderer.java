package flounder.fonts;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * A renderer capable of rendering fonts.
 */
public class FontRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile(Shader.SHADERS_LOC, "fonts", "fontVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(Shader.SHADERS_LOC, "fonts", "fontFragment.glsl");

	private Shader shader;

	private boolean lastWireframe;

	/**
	 * Creates a new font renderer.
	 */
	public FontRenderer() {
		shader = Shader.newShader("fonts").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).createInSecondThread();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (!shader.isLoaded() || FlounderEngine.getFonts().getTexts().keySet().size() < 1) {
			return;
		}

		prepareRendering();
		FlounderEngine.getFonts().getTexts().keySet().forEach(font -> FlounderEngine.getFonts().getTexts().get(font).forEach(this::renderText));
		endRendering();
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Fonts", "Render Time", super.getRenderTimeMs());
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
		shader.getUniformFloat("aspectRatio").loadFloat(FlounderEngine.getDevices().getDisplay().getAspectRatio());
		shader.getUniformVec3("transform").loadVec3(textPosition.x, textPosition.y, text.getScale());
		shader.getUniformVec4("colour").loadVec4(textColour.getR(), textColour.getG(), textColour.getB(), text.getTransparency());
		shader.getUniformVec3("borderColour").loadVec3(text.getBorderColour());
		shader.getUniformVec2("edgeData").loadVec2(text.calculateEdgeStart(), text.calculateAntialiasSize());
		shader.getUniformVec2("borderSizes").loadVec2(text.getTotalBorderSize(), text.getGlowSize());
		glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
		OpenGlUtils.unbindVAO(0, 1);
	}
}
