package flounder.fbos;

import flounder.devices.*;
import flounder.logger.*;

/**
 * A class that represents a OpenGL Frame Buffer object.
 */
public class FBO {
	private DepthBufferType depthBufferType;
	private boolean useColourBuffer;
	private boolean linearFiltering;
	private boolean wrapTextures;
	private boolean clampEdge;
	private boolean alphaChannel;
	private boolean antialiased;
	private int samples;
	private int width;
	private int height;
	private int attachments;
	private boolean fitToScreen;
	private float sizeScalar;

	private int frameBuffer;
	private int colourTexture[];
	private int depthTexture;
	private int depthBuffer;
	private int colourBuffer[];

	private boolean hasGivenResolveError;

	/**
	 * A new OpenGL FBO object.
	 *
	 * @param width The FBO's width.
	 * @param height The FBO's height.
	 * @param attachments The amount of attachments to create.
	 * @param fitToScreen If the width and height values should match the screen.
	 * @param sizeScalar A scalar factor between the FBO and the screen, enabled when {@code fitToScreen} is enabled. (1.0f disables scalar).
	 * @param depthBufferType The type of depth buffer to use in the FBO.
	 * @param useColourBuffer If a colour buffer should be created.
	 * @param linearFiltering If linear filtering should be used.
	 * @param wrapTextures If textures will even be bothered with wrapping.
	 * @param clampEdge If the image should be clamped to the edges.
	 * @param alphaChannel If alpha should be supported.
	 * @param antialiased If the image will be antialiased.
	 * @param samples How many MFAA samples should be used on the FBO. Zero disables multisampling.
	 */
	protected FBO(int width, int height, int attachments, boolean fitToScreen, float sizeScalar, DepthBufferType depthBufferType, boolean useColourBuffer, boolean linearFiltering, boolean wrapTextures, boolean clampEdge, boolean alphaChannel, boolean antialiased, int samples) {
		this.fitToScreen = fitToScreen;
		this.sizeScalar = sizeScalar;
		this.depthBufferType = depthBufferType;
		this.useColourBuffer = useColourBuffer;
		this.linearFiltering = linearFiltering;
		this.wrapTextures = wrapTextures;
		this.clampEdge = clampEdge;
		this.alphaChannel = alphaChannel;
		this.antialiased = antialiased;
		this.samples = samples;
		this.width = (int) (width * sizeScalar);
		this.height = (int) (height * sizeScalar);
		this.attachments = attachments;

		this.colourTexture = new int[attachments];
		this.colourBuffer = new int[attachments];

		this.hasGivenResolveError = false;

		FlounderFBOs.get().initializeFBO(this);
	}

	/**
	 * Creates a new FBO Builder.
	 *
	 * @param width The initial width for the new FBO.
	 * @param height The initial height for the new FBO.
	 *
	 * @return A new FBO Builder.
	 */
	public static FBOBuilder newFBO(int width, int height) {
		return new FBOBuilder(width, height);
	}

	/**
	 * Creates a new FBO Builder, fit to the screen.
	 *
	 * @param sizeScalar A scalar factor between the FBO and the screen, enabled when {@code fitToScreen} is enabled. (1.0f disables scalar).
	 *
	 * @return A new FBO Builder.
	 */
	public static FBOBuilder newFBO(float sizeScalar) {
		return new FBOBuilder(FlounderDisplay.get().getWidth(), FlounderDisplay.get().getHeight()).fitToScreen(sizeScalar);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Gets the number of attachments in this FBO.
	 *
	 * @return The number of attachments in this FBO.
	 */
	public int getAttachments() {
		return attachments;
	}

	public void setAttachments(int attachments) {
		this.attachments = attachments;
	}

	/**
	 * Gets the number of antialiasing samples.
	 *
	 * @return The number of antialiasing samples.
	 */
	public int getSamples() {
		return samples;
	}

	/**
	 * Sets the number antialiasing samples, and recreates the FBO.
	 *
	 * @param samples The number of antialiasing samples.
	 */
	public void setSamples(int samples) {
		if (this.samples != samples) {
			delete();
			FlounderLogger.get().log("Recreating FBO: width: " + width + ", and height: " + height + ".");
			FlounderFBOs.get().initializeFBO(this);
		}

		this.samples = samples;
	}

	public boolean isFitToScreen() {
		return fitToScreen;
	}

	public void setFitToScreen(boolean fitToScreen) {
		this.fitToScreen = fitToScreen;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		fitToScreen = false;
		delete();
		FlounderLogger.get().log("Recreating FBO: width: " + width + ", and height: " + height + ".");
		FlounderFBOs.get().initializeFBO(this);
	}

	public void bindFrameBuffer() {
		FlounderFBOs.get().bindFrameBuffer(this);
	}

	public void unbindFrameBuffer() {
		FlounderFBOs.get().unbindFrameBuffer();
	}

	public void blitToScreen() {
		FlounderFBOs.get().blitToScreen(this);
	}

	public float getSizeScalar() {
		return sizeScalar;
	}

	public void setSizeScalar(float sizeScalar) {
		if (this.fitToScreen && this.sizeScalar == sizeScalar) {
			return;
		}

		this.sizeScalar = sizeScalar;
		this.fitToScreen = true;
		updateSize();
	}

	/**
	 * Updates the FBO size if {@code fitToScreen}.
	 */
	public void updateSize() {
		if (fitToScreen) {
			int displayWidth = FlounderDisplay.get().getWidth();
			int displayHeight = FlounderDisplay.get().getHeight();
			int reverseWidth = (int) (displayWidth * sizeScalar);
			int reverseHeight = (int) (displayHeight * sizeScalar);

			if (displayWidth == 0 || displayHeight == 0) {
				return;
			}

			if (width != reverseWidth || height != reverseHeight) {
				int newWidth = (int) (displayWidth * sizeScalar);
				int newHeight = (int) (displayHeight * sizeScalar);
				//	if (newWidth < FlounderFBOs.get().getMaxFBOSize() && newHeight < FlounderFBOs.get().getMaxFBOSize()) { // TODO: Fix this ghetto way of fixing the creation of millions of FBOs on old PCs.
				width = newWidth;
				height = newHeight;
				//	}
				FlounderFBOs.get().limitFBOSize(this);

				delete();
				FlounderLogger.get().log("Recreating FBO: width: " + width + ", and height: " + height + ".");
				FlounderFBOs.get().initializeFBO(this);
			}
		}
	}

	/**
	 * Deletes the FBO and its attachments.
	 */
	public void delete() {
		FlounderFBOs.get().delete(this);
	}

	/**
	 * Gets a colour buffer.
	 *
	 * @param readBuffer The colour attachment to be read from.
	 *
	 * @return The OpenGL colour texture id.
	 */
	public int getColourTexture(int readBuffer) {
		return colourTexture[readBuffer];
	}

	public DepthBufferType getDepthBufferType() {
		return depthBufferType;
	}

	/**
	 * Gets the depth texture.
	 *
	 * @return The OpenGL depth texture id.
	 */
	public int getDepthTexture() {
		return depthTexture;
	}

	public void setDepthTexture(int depthTexture) {
		this.depthTexture = depthTexture;
	}

	public int getFrameBuffer() {
		return frameBuffer;
	}

	public void setFrameBuffer(int frameBuffer) {
		this.frameBuffer = frameBuffer;
	}

	public int[] getColourTexture() {
		return colourTexture;
	}

	public void setColourTexture(int[] colourTexture) {
		this.colourTexture = colourTexture;
	}

	public int getDepthBuffer() {
		return depthBuffer;
	}

	public void setDepthBuffer(int depthBuffer) {
		this.depthBuffer = depthBuffer;
	}

	public int[] getColourBuffer() {
		return colourBuffer;
	}

	public void setColourBuffer(int[] colourBuffer) {
		this.colourBuffer = colourBuffer;
	}

	public boolean isUseColourBuffer() {
		return useColourBuffer;
	}

	public boolean isLinearFiltering() {
		return linearFiltering;
	}

	public boolean isWrapTextures() {
		return wrapTextures;
	}

	public boolean isClampEdge() {
		return clampEdge;
	}

	public boolean isAlphaChannel() {
		return alphaChannel;
	}

	public boolean isAntialiased() {
		return antialiased;
	}

	public boolean isHasGivenResolveError() {
		return hasGivenResolveError;
	}

	public void setHasGivenResolveError(boolean hasGivenResolveError) {
		this.hasGivenResolveError = hasGivenResolveError;
	}
}
