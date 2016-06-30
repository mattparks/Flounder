package flounder.guis;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;

public class GuiRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile("flounder/guis", "guiVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("flounder/guis", "guiFragment.glsl");

	private static final float[] POSITIONS = {0, 0, 0, 1, 1, 0, 1, 1};

	private ShaderProgram shader;
	private int vaoID;

	private boolean lastWireframe;

	public GuiRenderer() {
		shader = new ShaderProgram("gui", VERTEX_SHADER, FRAGMENT_SHADER);
		vaoID = FlounderEngine.getLoader().createInterleavedVAO(POSITIONS, 2);
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (FlounderEngine.getGuis().getGuiTextures().size() < 1) {
			return;
		}

		prepareRendering();
		FlounderEngine.getGuis().getGuiTextures().forEach(this::renderGui);
		endRendering();
	}

	@Override
	public void profile() {
		if (FlounderEngine.getProfiler().isOpen()) {
			FlounderEngine.getProfiler().add("GUIs", "Render Time", super.getRenderTimeMs());
		}
	}

	private void prepareRendering() {
		shader.start();

		lastWireframe = OpenGlUtils.isInWireframe();

		OpenGlUtils.antialias(false);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.enableAlphaBlending();
		OpenGlUtils.disableDepthTesting();
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

	private void renderGui(GuiTexture gui) {
		if (!gui.getTexture().isLoaded()) {
			return;
		}

		OpenGlUtils.bindVAO(vaoID, 0);
		OpenGlUtils.bindTextureToBank(gui.getTexture().getTextureID(), 0);
		shader.getUniformVec4("transform").loadVec4(gui.getPosition().x, gui.getPosition().y, gui.getScale().x, gui.getScale().y);
		shader.getUniformFloat("alpha").loadFloat(gui.getAlpha());
		shader.getUniformBool("flipTexture").loadBoolean(gui.isFlipTexture());
		shader.getUniformFloat("atlasRows").loadFloat(gui.getTexture().getNumberOfRows());
		shader.getUniformVec2("atlasOffset").loadVec2(gui.getTextureOffset());
		shader.getUniformVec3("colourOffset").loadVec3(gui.getColourOffset());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, POSITIONS.length / 2);
		OpenGlUtils.unbindVAO(0);
	}
}
