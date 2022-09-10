package com.flounder.fbos;

import com.flounder.devices.*;
import com.flounder.logger.*;
import com.flounder.platform.FlounderPlatform;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_MAX_RENDERBUFFER_SIZE_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;

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

		initializeFBO();
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

	/**
	 * Initializes the FBO.
	 */
	public void initializeFBO() {
		createFBO();
		limitFBOSize();

		if (!antialiased) {
			if (useColourBuffer) {
				for (int i = 0; i < attachments; i++) {
					createTextureAttachment(GL_COLOR_ATTACHMENT0 + i);
				}
			}

			if (depthBufferType == DepthBufferType.RENDER_BUFFER) {
				createDepthBufferAttachment();
			} else if (depthBufferType == DepthBufferType.TEXTURE) {
				createDepthTextureAttachment();
			}
		} else {
			for (int i = 0; i < attachments; i++) {
				attachMultisampleColourBuffer( GL_COLOR_ATTACHMENT0 + i);
			}

			createDepthBufferAttachment();
		}

		unbindFrameBuffer();
	}

	public void createFBO() {
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

		if (useColourBuffer) {
			determineDrawBuffers();
		} else {
			glDrawBuffer(GL_FALSE);
		}
	}

	public void determineDrawBuffers() {
		IntBuffer drawBuffers = FlounderPlatform.get().createIntBuffer(attachments);

		for (int i = 0; i < attachments; i++) {
			drawBuffers.put(GL_COLOR_ATTACHMENT0 + i);
		}

		drawBuffers.flip();
		glDrawBuffers(drawBuffers);
	}

	public void createTextureAttachment(int attachment) {
		colourTexture[attachment - GL_COLOR_ATTACHMENT0] = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colourTexture[attachment - GL_COLOR_ATTACHMENT0]);
		glTexImage2D(GL_TEXTURE_2D, 0, alphaChannel ? GL_RGBA : GL_RGB, width, height, 0, alphaChannel ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, linearFiltering ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, linearFiltering ? GL_LINEAR : GL_NEAREST);

		if (wrapTextures) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, clampEdge ? GL_CLAMP_TO_EDGE : GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, clampEdge ? GL_CLAMP_TO_EDGE : GL_REPEAT);
		}

		glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, colourTexture[attachment - GL_COLOR_ATTACHMENT0], 0);
	}

	public void createDepthBufferAttachment() {
		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);

		if (antialiased) {
			glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT24, width, height);
		} else {
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
		}

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
	}

	public void createDepthTextureAttachment() {
		depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
	}

	public void attachMultisampleColourBuffer(int attachment) {
		colourBuffer[attachment - GL_COLOR_ATTACHMENT0] = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colourBuffer[attachment - GL_COLOR_ATTACHMENT0]);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, alphaChannel ? GL_RGBA8 : GL_RGB8, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, colourBuffer[attachment - GL_COLOR_ATTACHMENT0]);
	}

	/**
	 * Blits this FBO and all attachments to another FBO.
	 *
	 * @param outputFBO The other FBO to blit to.
	 */
	public void resolveFBO(FBO outputFBO) {
		if (this.attachments != outputFBO.getAttachments() && this.hasGivenResolveError != outputFBO.hasGivenResolveError) {
			this.hasGivenResolveError = true;
			outputFBO.hasGivenResolveError = true;
			FlounderLogger.get().log("Warning, resolving two FBO's (" + this + ", " + outputFBO + ") with different attachment sizes, be warned this may not work properly instead use resolveFBO(int readBuffer, int drawBuffer, FBO outputFBO).");
		}

		for (int a = 0; a < attachments; a++) {
			resolveFBO(a, a, outputFBO);
		}
	}

	/**
	 * Blits this FBO attachment to another FBO attachment.
	 *
	 * @param readBuffer The colour attachment to be read from.
	 * @param drawBuffer The colour draw buffer to be written to.
	 * @param outputFBO The other FBO to blit to.
	 */
	public void resolveFBO(int readBuffer, int drawBuffer, FBO outputFBO) {
		outputFBO.updateSize();
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFBO.frameBuffer);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);

		glReadBuffer(GL_COLOR_ATTACHMENT0 + readBuffer);
		glDrawBuffer(GL_COLOR_ATTACHMENT0 + drawBuffer);
		glBlitFramebuffer(0, 0, width, height, 0, 0, outputFBO.getWidth(), outputFBO.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);

		unbindFrameBuffer();
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
				//	if (newWidth < getMaxFBOSize() && newHeight < getMaxFBOSize()) { // TODO: Fix this ghetto way of fixing the creation of millions of FBOs on old PCs.
				width = newWidth;
				height = newHeight;
				//	}
				limitFBOSize();

				delete();
				FlounderLogger.get().log("Recreating FBO: width: " + width + ", and height: " + height + ".");
				initializeFBO();
			}
		}
	}

	public void limitFBOSize() {
		width = Math.min(getMaxFBOSize(), width);
		height = Math.min(getMaxFBOSize(), height);
	}

	public void setSizeScalar(float sizeScalar) {
		if (this.fitToScreen && this.sizeScalar == sizeScalar) {
			return;
		}

		this.sizeScalar = sizeScalar;
		this.fitToScreen = true;
		updateSize();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Gets the number of attachments in this FBO.
	 *
	 * @return The number of attachments in this FBO.
	 */
	public int getAttachments() {
		return attachments;
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
			initializeFBO();
		}

		this.samples = samples;
	}

	public boolean isFitToScreen() {
		return fitToScreen;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		fitToScreen = false;
		delete();
		FlounderLogger.get().log("Recreating FBO: width: " + width + ", and height: " + height + ".");
		initializeFBO();
	}

	/**
	 * Binds the FBO so it can be rendered too.
	 */
	public void bindFrameBuffer() {
		updateSize();
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
		glViewport(0, 0, width, height);
	}

	/**
	 * Unbinds the FBO so that other rendering objects can be used.
	 */
	public void unbindFrameBuffer() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, FlounderDisplay.get().getWidth(), FlounderDisplay.get().getHeight());
	}

	/**
	 * Renders the colour buffer to the display.
	 */
	public void blitToScreen() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glDrawBuffer(GL_BACK);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
		glBlitFramebuffer(0, 0, width, height, 0, 0, FlounderDisplay.get().getWidth(), FlounderDisplay.get().getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}

	public float getSizeScalar() {
		return sizeScalar;
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

	public static int getMaxFBOSize() {
		return glGetInteger(GL_MAX_RENDERBUFFER_SIZE_EXT);
	}

	/**
	 * Deletes the FBO and its attachments.
	 */
	public void delete() {
		glDeleteFramebuffers(frameBuffer);
		glDeleteTextures(colourTexture);
		glDeleteTextures(depthTexture);
		glDeleteRenderbuffers(depthBuffer);
		glDeleteRenderbuffers(colourBuffer);
	}
}
