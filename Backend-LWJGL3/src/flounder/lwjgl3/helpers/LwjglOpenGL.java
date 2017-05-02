package flounder.lwjgl3.helpers;

import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.textures.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

@Module.ModuleOverride
public class LwjglOpenGL extends FlounderOpenGL {
	private boolean cullingBackFace;
	private boolean depthMask;
	private boolean inWireframe;
	private boolean isAlphaBlending;
	private boolean additiveBlending;
	private boolean antialiasing;

	public LwjglOpenGL() {
		this(false);
	}

	public LwjglOpenGL(boolean wireframe) {
		super();
		this.cullingBackFace = false;
		this.depthMask = true;
		this.inWireframe = wireframe;
		this.isAlphaBlending = false;
		this.additiveBlending = false;
		this.antialiasing = false;
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {

		super.init();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		super.update();

	}

	@Override
	public boolean isModern() {
		int major = glGetInteger(GL_MAJOR_VERSION);
		int minor = glGetInteger(GL_MINOR_VERSION);
		return major > 3 || (major == 3 && minor > 0); // major == 3 && minor > 3
	}

	@Override
	public void prepareNewRenderParse(Colour colour) {
		prepareNewRenderParse(colour.getR(), colour.getG(), colour.getB());
	}

	@Override
	public void prepareNewRenderParse(float r, float g, float b) {
		glClearColor(r, g, b, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		disableBlending();
		cullBackFaces(true);
		enableDepthTesting();
	}

	@Override
	public void cullBackFaces(boolean cull) {
		if (cull && !cullingBackFace) {
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			cullingBackFace = true;
		} else if (!cull && cullingBackFace) {
			glDisable(GL_CULL_FACE);
			cullingBackFace = false;
		}
	}

	@Override
	public void enableDepthTesting() {
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void disableDepthTesting() {
		glDisable(GL_DEPTH_TEST);
	}

	@Override
	public void depthMask(boolean depthMask) {
		this.depthMask = depthMask;
		glDepthMask(depthMask);
	}

	@Override
	public boolean isInWireframe() {
		return inWireframe;
	}

	@Override
	public void goWireframe(boolean goWireframe) {
		if (goWireframe && !inWireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			inWireframe = true;
		} else if (!goWireframe && inWireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			inWireframe = false;
		}
	}

	@Override
	public void enableAlphaBlending() {
		if (!isAlphaBlending) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			isAlphaBlending = true;
			additiveBlending = false;
		}
	}

	@Override
	public void enableAdditiveBlending() {
		if (!additiveBlending) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE);
			additiveBlending = true;
			isAlphaBlending = false;
		}
	}

	@Override
	public void disableBlending() {
		if (isAlphaBlending || additiveBlending) {
			glDisable(GL_BLEND);
			isAlphaBlending = false;
			additiveBlending = false;
		}
	}

	@Override
	public void antialias(boolean enable) {
		if (enable && !antialiasing) {
			glEnable(GL_MULTISAMPLE);
			antialiasing = true;
		} else if (!enable && antialiasing) {
			glDisable(GL_MULTISAMPLE);
			antialiasing = false;
		}
	}

	@Override
	public void bindVAO(int vaoID, int... attributes) {
		glBindVertexArray(vaoID);

		for (int i : attributes) {
			glEnableVertexAttribArray(i);
		}
	}

	@Override
	public void unbindVAO(int... attributes) {
		for (int i : attributes) {
			glDisableVertexAttribArray(i);
		}

		glBindVertexArray(0);
	}

	@Override
	public void bindTexture(TextureObject texture, int bankID) {
		if (texture == null || texture.getTextureID() == -1) {
			return;
		}

		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(texture.getGlType(), texture.getTextureID());
	}

	@Override
	public void bindTexture(int textureID, int glTarget, int bankID) {
		if (textureID == -1) {
			return;
		}

		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(glTarget, textureID);
	}

	@Override
	public void bindTextureLOD(int textureID, int lodBias, int bankID) {
		if (textureID == -1) {
			return;
		}

		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, lodBias);
		glActiveTexture(0);
	}

	@Override
	public void renderArrays(int glMode, int glLength) {
		glDrawArrays(glMode, 0, glLength);
	}

	@Override
	public void renderElements(int glMode, int glType, int glLength) {
		glDrawElements(glMode, glLength, glType, 0);
	}

	@Override
	public void renderInstanced(int glMode, int glLength, int glPrimCount) {
		if (isModern()) {
			glDrawArraysInstanced(glMode, 0, glLength, glPrimCount);
		}// else {
		//	ARBDrawInstanced.glDrawArraysInstancedARB(glMode, 0, glLength, glPrimCount);
		//}
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		super.profile();
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();

	}
}
