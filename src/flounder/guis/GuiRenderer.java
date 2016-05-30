package flounder.guis;

import flounder.engine.*;
import flounder.loaders.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;

import static org.lwjgl.opengl.GL11.*;

public class GuiRenderer extends IRenderer {
	private static final float[] POSITIONS = {0, 0, 0, 1, 1, 0, 1, 1};

	private final GuiShader shader;
	private final int vaoID;

	private int guiCount;
	private boolean lastWireframe;

	public GuiRenderer() {
		shader = new GuiShader();
		vaoID = Loader.createInterleavedVAO(POSITIONS, 2);

		guiCount = 0;
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		if (GuiManager.getGuiTextures().size() < 1) {
			return;
		}

		prepareRendering();
		GuiManager.getGuiTextures().forEach(this::renderGui);
		endRendering();

		FlounderProfiler.add("GUI", "Render Count", guiCount);
		FlounderProfiler.add("GUI", "Render Time", super.getRenderTimeMs());
		guiCount = 0;
	}

	private void prepareRendering() {
		shader.start();

		lastWireframe = OpenglUtils.isInWireframe();

		OpenglUtils.antialias(false);
		OpenglUtils.cullBackFaces(true);
		OpenglUtils.enableAlphaBlending();
		OpenglUtils.disableDepthTesting();
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

	private void renderGui(final GuiTexture gui) {
		if (!gui.getTexture().isLoaded()) {
			return;
		}

		guiCount++;
		OpenglUtils.bindVAO(vaoID, 0);
		OpenglUtils.bindTextureToBank(gui.getTexture().getTextureID(), 0);
		shader.transform.loadVec4(gui.getPosition().x, gui.getPosition().y, gui.getScale().x, gui.getScale().y);
		shader.alpha.loadFloat(gui.getAlpha());
		shader.flipTexture.loadBoolean(gui.isFlipTexture());
		shader.atlasRows.loadFloat(gui.getTexture().getNumberOfRows());
		shader.atlasOffset.loadVec2(gui.getTextureOffset());
		shader.colourOffset.loadVec3(gui.getColourOffset());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, POSITIONS.length / 2);
		OpenglUtils.unbindVAO(0);
	}
}
