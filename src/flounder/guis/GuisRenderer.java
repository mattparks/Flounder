package flounder.guis;

import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import java.util.*;

import static flounder.platform.Constants.*;

public class GuisRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "guis", "guiVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "guis", "guiFragment.glsl");

	private ShaderObject shader;
	private int vaoID;
	private int vaoLength;

	public GuisRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("guis").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
		this.vaoID = FlounderLoader.createInterleavedVAO(FlounderGuis.POSITIONS, 2);
		this.vaoLength = FlounderGuis.POSITIONS.length / 2;
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || FlounderGuis.getContainer() == null) {
			return;
		}

		prepareRendering();
		FlounderGuis.getContainer().getAll(new ArrayList<>()).forEach(this::renderGui);
		endRendering();
	}

	private void prepareRendering() {
		shader.start();

		OpenGlUtils.antialias(false);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.enableAlphaBlending();
		OpenGlUtils.disableDepthTesting();

		shader.getUniformFloat("aspectRatio").loadFloat(FlounderDisplay.getAspectRatio());
		shader.getUniformBool("polygonMode").loadBoolean(OpenGlUtils.isInWireframe());
	}

	private void renderGui(ScreenObject object) {
		if (!(object instanceof GuiObject)) {
			return;
		}

		GuiObject gui = (GuiObject) object;

		if (vaoID == -1 || !gui.getTexture().isLoaded() || !gui.isVisible()) {
			return;
		}

		OpenGlUtils.bindVAO(vaoID, 0);
		OpenGlUtils.bindTexture(gui.getTexture(), 0);
		//	GL11.glEnable(GL11.GL_SCISSOR_TEST); // TODO: Scissor test for scroll panels.
		//	GL11.glScissor(x, y, width, height);
		shader.getUniformVec2("size").loadVec2(gui.getMeshSize());
		shader.getUniformVec4("transform").loadVec4(
				gui.getScreenPosition().x, gui.getScreenPosition().y,
				gui.getScreenDimensions().x, gui.getScreenDimensions().y
		);
		shader.getUniformFloat("rotation").loadFloat((float) Math.toRadians(gui.getRotation()));

		shader.getUniformFloat("alpha").loadFloat(gui.getAlpha());
		shader.getUniformBool("flipTexture").loadBoolean(gui.isFlipTexture());
		shader.getUniformFloat("atlasRows").loadFloat(gui.getTexture().getNumberOfRows());
		shader.getUniformVec2("atlasOffset").loadVec2(gui.getTextureOffset());
		shader.getUniformVec3("colourOffset").loadVec3(gui.getColourOffset());
		OpenGlUtils.renderArrays(GL_TRIANGLE_STRIP, vaoLength);
		OpenGlUtils.unbindVAO(0);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void profile() {
		FlounderProfiler.add(FlounderGuis.PROFILE_TAB_NAME, "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		if (vaoID != -1) {
			FlounderLoader.deleteVAOFromCache(vaoID);
			vaoID = -1;
		}

		shader.delete();
	}
}
