package flounder.helpers;

import flounder.framework.*;
import flounder.maths.*;
import flounder.textures.*;

public class FlounderOpenGL extends Module {
	public FlounderOpenGL() {
		super();
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
	@Module.MethodReplace
	public boolean isModern() {
		return true;
	}

	/**
	 * Prepares the screen for a new render.
	 *
	 * @param colour The clear colour.
	 */
	@Module.MethodReplace
	public void prepareNewRenderParse(Colour colour) {
	}

	/**
	 * Prepares the screen for a new render.
	 *
	 * @param r The r component of the clear colour.
	 * @param g The g component of the clear colour.
	 * @param b The b component of the clear colour.
	 */
	@Module.MethodReplace
	public void prepareNewRenderParse(float r, float g, float b) {
	}

	/**
	 * Toggles the culling of back-faces.
	 *
	 * @param cull Should back faces be culled.
	 */
	@Module.MethodReplace
	public void cullBackFaces(boolean cull) {
	}

	/**
	 * Enables depth testing.
	 */
	@Module.MethodReplace
	public void enableDepthTesting() {
	}

	/**
	 * Disables depth testing.
	 */
	@Module.MethodReplace
	public void disableDepthTesting() {
	}

	@Module.MethodReplace
	public void depthMask(boolean depthMask) {
	}

	/**
	 * @return Is the display currently in wireframe mode.
	 */
	@Module.MethodReplace
	public boolean isInWireframe() {
		return false;
	}

	/**
	 * Toggles the display to / from wireframe mode.
	 *
	 * @param goWireframe If the display should be in wireframe.
	 */
	@Module.MethodReplace
	public void goWireframe(boolean goWireframe) {
	}

	/**
	 * Enables alpha blending.
	 */
	@Module.MethodReplace
	public void enableAlphaBlending() {
	}

	/**
	 * Enables additive blending.
	 */
	@Module.MethodReplace
	public void enableAdditiveBlending() {
	}

	/**
	 * Disables alpha and additive blending.
	 */
	@Module.MethodReplace
	public void disableBlending() {
	}

	/**
	 * Toggles antialiasing for the rendered object.
	 *
	 * @param enable Should antialias be enabled?
	 */
	@Module.MethodReplace
	public void antialias(boolean enable) {
	}

	/**
	 * Binds the VAO and all attributes.
	 *
	 * @param vaoID The VAO to bind.
	 * @param attributes Attributes to enable.
	 */
	@Module.MethodReplace
	public void bindVAO(int vaoID, int... attributes) {
	}

	/**
	 * Unbinds the current VAO and all attributes.
	 *
	 * @param attributes Attributes to disable.
	 */
	@Module.MethodReplace
	public void unbindVAO(int... attributes) {
	}

	@Module.MethodReplace
	public void enable(int gl) {

	}

	@Module.MethodReplace
	public void disable(int gl) {

	}

	@Module.MethodReplace
	public void scissor(int x, int y, int width, int height) {

	}

	/**
	 * Binds a OpenGL texture to a blank ID.
	 *
	 * @param texture The texture to bind.
	 * @param bankID The shaders blank ID to bind to.
	 */
	@Module.MethodReplace
	public void bindTexture(TextureObject texture, int bankID) {
	}

	/**
	 * Binds a OpenGL texture to a blank ID.
	 *
	 * @param textureID The texture to bind.
	 * @param glTarget The OpenGL texture type to bind to. {@code GL_TEXTURE_CUBE_MAP}, and {@code GL_TEXTURE_2D} are the most common types.
	 * @param bankID The shaders blank ID to bind to.
	 */
	@Module.MethodReplace
	public void bindTexture(int textureID, int glTarget, int bankID) {
	}

	/**
	 * Binds the OpenGL texture to a blank ID.
	 *
	 * @param textureID The texture to bind.
	 * @param lodBias The LOD to load to texture at.
	 * @param bankID The shaders blank ID to bind to.
	 */
	@Module.MethodReplace
	public void bindTextureLOD(int textureID, int lodBias, int bankID) {
	}

	/**
	 * Renders a bound model on a enabled shader using glDrawArrays.
	 *
	 * @param glMode The OpenGL mode to draw in.
	 * @param glLength The length of the model.
	 */
	@Module.MethodReplace
	public void renderArrays(int glMode, int glLength) {
	}

	/**
	 * Renders a bound model on a enabled shader using glDrawElements.
	 *
	 * @param glMode The OpenGL mode to draw in.
	 * @param glType The OpenGL type to draw in.
	 * @param glLength The length of the model.
	 */
	@Module.MethodReplace
	public void renderElements(int glMode, int glType, int glLength) {
	}

	/**
	 * Renders a bound model on a enabled shader using glDrawArraysInstancedARB.
	 *
	 * @param glMode The OpenGL mode to draw in.
	 * @param glLength The length of the model.
	 * @param glPrimCount How many primitives rendered.
	 */
	@Module.MethodReplace
	public void renderInstanced(int glMode, int glLength, int glPrimCount) {
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Instance
	public static FlounderOpenGL get() {
		return (FlounderOpenGL) Framework.get().getInstance(FlounderOpenGL.class);
	}

	@TabName
	public static String getTab() {
		return "OpenGL";
	}
}
