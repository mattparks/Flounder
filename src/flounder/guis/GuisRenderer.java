package flounder.guis;

import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.maths.vectors.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import static flounder.platform.Constants.*;

public class GuisRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "guis", "guiVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "guis", "guiFragment.glsl");

	private ShaderObject shader;
	private int vaoID;
	private int vaoLength;

	public GuisRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("guis").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
		this.vaoID = FlounderLoader.get().createInterleavedVAO(FlounderGuis.POSITIONS, 2);
		this.vaoLength = FlounderGuis.POSITIONS.length / 2;
	}

	@Override
	public void render(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || FlounderGuis.get().getContainer() == null || FlounderGuis.get().getObjects().isEmpty()) {
			return;
		}

		prepareRendering();
		FlounderGuis.get().getObjects().forEach(this::renderGui);
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

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void dispose() {
		if (vaoID != -1) {
			FlounderLoader.get().deleteVAOFromCache(vaoID);
			vaoID = -1;
		}

		shader.delete();
	}

	private void renderGui(ScreenObject object) {
		if (!(object instanceof GuiObject)) {
			return;
		}

		GuiObject gui = (GuiObject) object;

		if (vaoID == -1 || gui.getTexture() == null || !gui.getTexture().isLoaded() || !gui.isVisible()) {
			return;
		}

		FlounderOpenGL.get().bindVAO(vaoID, 0);
		FlounderOpenGL.get().bindTexture(gui.getTexture(), 0);

		Vector4f scissor = object.getScissor();

		if (scissor.getZ() != -1.0f && scissor.getW() != -1.0f) {
			FlounderOpenGL.get().enable(GL_SCISSOR_TEST);
			FlounderOpenGL.get().scissor((int) scissor.x, (int) scissor.y, (int) scissor.z, (int) scissor.w);
		}

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
		FlounderOpenGL.get().renderArrays(GL_TRIANGLE_STRIP, vaoLength);
		FlounderOpenGL.get().unbindVAO(0);
		FlounderOpenGL.get().disable(GL_SCISSOR_TEST);
	}
}
