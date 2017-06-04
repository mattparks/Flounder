package flounder.fonts;

import flounder.camera.*;
import flounder.devices.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import static flounder.platform.Constants.*;

/**
 * A renderer capable of rendering fonts.
 */
public class FontRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "fonts", "fontVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "fonts", "fontFragment.glsl");

	private ShaderObject shader;

	/**
	 * Creates a new font renderer.
	 */
	public FontRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("fonts").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
	}

	@Override
	public void render(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || FlounderGuis.get().getContainer() == null || FlounderGuis.get().getObjects().isEmpty()) {
			return;
		}

		prepareRendering();
		FlounderGuis.get().getObjects().forEach(this::renderText);
		endRendering();
	}

	private void prepareRendering() {
		shader.start();

		FlounderOpenGL.get().antialias(false);
		FlounderOpenGL.get().cullBackFaces(true);
		FlounderOpenGL.get().enableAlphaBlending();
		FlounderOpenGL.get().disableDepthTesting();

		shader.getUniformFloat("aspectRatio").loadFloat(FlounderDisplay.get().getAspectRatio());
		shader.getUniformBool("polygonMode").loadBoolean(FlounderOpenGL.get().isInWireframe());
	}

	private void renderText(ScreenObject object) {
		if (!(object instanceof TextObject)) {
			return;
		}

		TextObject text = (TextObject) object;

		if (!text.isLoaded() || !text.isVisible()) {
			return;
		}

		FlounderOpenGL.get().bindVAO(text.getMesh(), 0, 1);
		FlounderOpenGL.get().bindTexture(text.getFont().getTexture(), 0);

		Vector4f scissor = object.getScissor();

		if (scissor.getZ() != -1.0f && scissor.getW() != -1.0f) {
			FlounderOpenGL.get().enable(GL_SCISSOR_TEST);
			FlounderOpenGL.get().scissor((int) scissor.x, (int) scissor.y, (int) scissor.z, (int) scissor.w);
		}

		shader.getUniformVec2("size").loadVec2(text.getMeshSize());
		shader.getUniformVec4("transform").loadVec4(
				text.getScreenPosition().x, text.getScreenPosition().y,
				text.getScreenDimensions().x, text.getScreenDimensions().y
		);
		shader.getUniformFloat("rotation").loadFloat((float) Math.toRadians(text.getRotation()));

		shader.getUniformVec4("colour").loadVec4(text.getColour().r, text.getColour().g, text.getColour().b, text.getAlpha());
		shader.getUniformVec3("borderColour").loadVec3(text.getBorderColour());
		shader.getUniformVec2("edgeData").loadVec2(text.calculateEdgeStart(), text.calculateAntialiasSize());
		shader.getUniformVec2("borderSizes").loadVec2(text.getTotalBorderSize(), text.getGlowSize());
		FlounderOpenGL.get().renderArrays(GL_TRIANGLES, text.getVertexCount());
		FlounderOpenGL.get().unbindVAO(0, 1);
		FlounderOpenGL.get().disable(GL_SCISSOR_TEST);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}
