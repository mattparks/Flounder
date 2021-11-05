package flounder.renderer;

import flounder.framework.*;
import flounder.maths.*;
import flounder.textures.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

public class FlounderOpenGL extends flounder.framework.Module {
	private boolean cullingBackFace;
	private boolean depthMask;
	private boolean inWireframe;
	private boolean isAlphaBlending;
	private boolean additiveBlending;
	private boolean antialiasing;

	public FlounderOpenGL() {
		this(false);
	}

	public FlounderOpenGL(boolean wireframe) {
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
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Gets if the computer is modern and can handle higher than OpenGL 3.3 functions.
	 *
	 * @return If the computer is modern.
	 */
	public boolean isModern() {
		int major = glGetInteger(GL_MAJOR_VERSION);
		int minor = glGetInteger(GL_MINOR_VERSION);
		return major > 3 || (major == 3 && minor > 0); // major == 3 && minor > 3
	}

	/**
	 * Prepares the screen for a new render.
	 *
	 * @param colour The clear colour.
	 */
	public void prepareNewRenderParse(Colour colour) {
		prepareNewRenderParse(colour.getR(), colour.getG(), colour.getB());
	}

	/**
	 * Prepares the screen for a new render.
	 *
	 * @param r The r component of the clear colour.
	 * @param g The g component of the clear colour.
	 * @param b The b component of the clear colour.
	 */
	public void prepareNewRenderParse(float r, float g, float b) {
		glClearColor(r, g, b, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		disableBlending();
		cullBackFaces(true);
		enableDepthTesting();
	}

	/**
	 * Toggles the culling of back-faces.
	 *
	 * @param cull Should back faces be culled.
	 */
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

	/**
	 * Enables depth testing.
	 */
	public void enableDepthTesting() {
		glEnable(GL_DEPTH_TEST);
	}

	/**
	 * Disables depth testing.
	 */
	public void disableDepthTesting() {
		glDisable(GL_DEPTH_TEST);
	}

	public void depthMask(boolean depthMask) {
		this.depthMask = depthMask;
		glDepthMask(depthMask);
	}

	/**
	 * @return Is the display currently in wireframe mode.
	 */
	public boolean isInWireframe() {
		return inWireframe;
	}

	/**
	 * Toggles the display to / from wireframe mode.
	 *
	 * @param goWireframe If the display should be in wireframe.
	 */
	public void goWireframe(boolean goWireframe) {
		if (goWireframe && !inWireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			inWireframe = true;
		} else if (!goWireframe && inWireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			inWireframe = false;
		}
	}

	/**
	 * Enables alpha blending.
	 */
	public void enableAlphaBlending() {
		if (!isAlphaBlending) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			isAlphaBlending = true;
			additiveBlending = false;
		}
	}

	/**
	 * Enables additive blending.
	 */
	public void enableAdditiveBlending() {
		if (!additiveBlending) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE);
			additiveBlending = true;
			isAlphaBlending = false;
		}
	}

	/**
	 * Disables alpha and additive blending.
	 */
	public void disableBlending() {
		if (isAlphaBlending || additiveBlending) {
			glDisable(GL_BLEND);
			isAlphaBlending = false;
			additiveBlending = false;
		}
	}

	/**
	 * Toggles antialiasing for the rendered object.
	 *
	 * @param enable Should antialias be enabled?
	 */
	public void antialias(boolean enable) {
		if (enable && !antialiasing) {
			glEnable(GL_MULTISAMPLE);
			antialiasing = true;
		} else if (!enable && antialiasing) {
			glDisable(GL_MULTISAMPLE);
			antialiasing = false;
		}
	}

	/**
	 * Binds the VAO and all attributes.
	 *
	 * @param vaoID The VAO to bind.
	 * @param attributes Attributes to enable.
	 */
	public void bindVAO(int vaoID, int... attributes) {
		glBindVertexArray(vaoID);

		for (int i : attributes) {
			glEnableVertexAttribArray(i);
		}
	}

	/**
	 * Unbinds the current VAO and all attributes.
	 *
	 * @param attributes Attributes to disable.
	 */
	public void unbindVAO(int... attributes) {
		for (int i : attributes) {
			glDisableVertexAttribArray(i);
		}

		glBindVertexArray(0);
	}

	public void enable(int gl) {
		glEnable(gl);
	}

	public void disable(int gl) {
		glDisable(gl);
	}

	public void scissor(int x, int y, int width, int height) {
		glScissor(x, y, width, height);
	}

	/**
	 * Binds a OpenGL texture to a blank ID.
	 *
	 * @param texture The texture to bind.
	 * @param bankID The shaders blank ID to bind to.
	 */
	public void bindTexture(TextureObject texture, int bankID) {
		if (texture == null || texture.getTextureID() == -1) {
			return;
		}

		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(texture.getGlType(), texture.getTextureID());
	}

	/**
	 * Binds a OpenGL texture to a blank ID.
	 *
	 * @param textureID The texture to bind.
	 * @param glTarget The OpenGL texture type to bind to. {@code GL_TEXTURE_CUBE_MAP}, and {@code GL_TEXTURE_2D} are the most common types.
	 * @param bankID The shaders blank ID to bind to.
	 */
	public void bindTexture(int textureID, int glTarget, int bankID) {
		if (textureID == -1) {
			return;
		}

		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(glTarget, textureID);
	}

	/**
	 * Binds the OpenGL texture to a blank ID.
	 *
	 * @param textureID The texture to bind.
	 * @param lodBias The LOD to load to texture at.
	 * @param bankID The shaders blank ID to bind to.
	 */
	public void bindTextureLOD(int textureID, int lodBias, int bankID) {
		if (textureID == -1) {
			return;
		}

		glActiveTexture(GL_TEXTURE0 + bankID);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, lodBias);
		glActiveTexture(0);
	}

	/**
	 * Renders a bound model on a enabled shader using glDrawArrays.
	 *
	 * @param glMode The OpenGL mode to draw in.
	 * @param glLength The length of the model.
	 */
	public void renderArrays(int glMode, int glLength) {
		glDrawArrays(glMode, 0, glLength);
	}

	/**
	 * Renders a bound model on a enabled shader using glDrawElements.
	 *
	 * @param glMode The OpenGL mode to draw in.
	 * @param glType The OpenGL type to draw in.
	 * @param glLength The length of the model.
	 */
	public void renderElements(int glMode, int glType, int glLength) {
		glDrawElements(glMode, glLength, glType, 0);
	}

	/**
	 * Renders a bound model on a enabled shader using glDrawArraysInstancedARB.
	 *
	 * @param glMode The OpenGL mode to draw in.
	 * @param glLength The length of the model.
	 * @param glPrimCount How many primitives rendered.
	 */
	public void renderInstanced(int glMode, int glLength, int glPrimCount) {
		if (isModern()) {
			glDrawArraysInstanced(glMode, 0, glLength, glPrimCount);
		}// else {
		//	ARBDrawInstanced.glDrawArraysInstancedARB(glMode, 0, glLength, glPrimCount);
		//}
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@flounder.framework.Module.Instance
	public static FlounderOpenGL get() {
		return (FlounderOpenGL) Framework.get().getModule(FlounderOpenGL.class);
	}
}
