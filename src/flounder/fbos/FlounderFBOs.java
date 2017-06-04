package flounder.fbos;

import flounder.framework.*;

/**
 * A module used for loading and managing OpenGL FBO's.
 */
public class FlounderFBOs extends Module {
	/**
	 * Creates a new OpenGL fbo manager class.
	 */
	public FlounderFBOs() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	@Module.MethodReplace
	public int getMaxFBOSize() {
		return 0;
	}

	/**
	 * Initializes the FBO.
	 */
	@Module.MethodReplace
	public void initializeFBO(FBO fbo) {
	}

	@Module.MethodReplace
	public void createFBO(FBO fbo) {
	}

	@Module.MethodReplace
	public void determineDrawBuffers(FBO fbo) {
	}

	@Module.MethodReplace
	public void createTextureAttachment(FBO fbo, int attachment) {
	}

	@Module.MethodReplace
	public void createDepthBufferAttachment(FBO fbo) {
	}

	@Module.MethodReplace
	public void createDepthTextureAttachment(FBO fbo) {
	}

	@Module.MethodReplace
	public void attachMultisampleColourBuffer(FBO fbo, int attachment) {
	}

	/**
	 * Binds the FBO so it can be rendered too.
	 */
	@Module.MethodReplace
	public void bindFrameBuffer(FBO fbo) {
	}

	/**
	 * Unbinds the FBO so that other rendering objects can be used.
	 */
	@Module.MethodReplace
	public void unbindFrameBuffer() {
	}

	/**
	 * Renders the colour buffer to the display.
	 */
	@Module.MethodReplace
	public void blitToScreen(FBO fbo) {
	}

	/**
	 * Blits this FBO and all attachments to another FBO.
	 *
	 * @param outputFBO The other FBO to blit to.
	 */
	@Module.MethodReplace
	public void resolveFBO(FBO fbo, FBO outputFBO) {
	}

	/**
	 * Blits this FBO attachment to another FBO attachment.
	 *
	 * @param readBuffer The colour attachment to be read from.
	 * @param drawBuffer The colour draw buffer to be written to.
	 * @param outputFBO The other FBO to blit to.
	 */
	@Module.MethodReplace
	public void resolveFBO(FBO fbo, int readBuffer, int drawBuffer, FBO outputFBO) {
	}

	@Module.MethodReplace
	public void limitFBOSize(FBO fbo) {
	}

	@Module.MethodReplace
	public void delete(FBO fbo) {
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Instance
	public static FlounderFBOs get() {
		return (FlounderFBOs) Framework.get().getInstance(FlounderFBOs.class);
	}

	@TabName
	public static String getTab() {
		return "FBOs";
	}
}
