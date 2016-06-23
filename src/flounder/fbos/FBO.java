package flounder.fbos;

import flounder.engine.*;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * A class that represents a OpenGL Frame Buffer object.
 */
public class FBO {
	private DepthBufferType depthBufferType;
	private boolean useColourBuffer;
	private boolean linearFiltering;
	private boolean clampEdge;
	private boolean alphaChannel;
	private boolean antialiased;
	private int samples;
	private int width;
	private int height;
	private boolean fitToScreen;
	private float sizeScalar;

	private int frameBuffer;
	private int colourTexture;
	private int depthTexture;
	private int depthBuffer;
	private int colourBuffer;

	/**
	 * A new OpenGL FBO object.
	 *
	 * @param width The FBO's width.
	 * @param height The FBO's height.
	 * @param fitToScreen If the width and height values should match the screen.
	 * @param sizeScalar A scalar factor between the FBO and the screen, enabled when {@code fitToScreen} is enabled. (1.0f disables scalar).
	 * @param depthBufferType The type of depth buffer to use in the FBO.
	 * @param useColourBuffer If a colour buffer should be created.
	 * @param linearFiltering If linear filtering should be used.
	 * @param clampEdge If the image should be clamped to the edges.
	 * @param alphaChannel If alpha should be supported.
	 * @param antialiased If the image will be antialiased.
	 * @param samples How many MFAA samples should be used on the FBO. Zero disables multisampling.
	 */
	protected FBO(int width, int height, boolean fitToScreen, float sizeScalar, DepthBufferType depthBufferType, boolean useColourBuffer, boolean linearFiltering, boolean clampEdge, boolean alphaChannel, boolean antialiased, int samples) {
		this.width = (int) (width * sizeScalar);
		this.height = (int) (height * sizeScalar);
		this.fitToScreen = fitToScreen;
		this.sizeScalar = sizeScalar;
		this.depthBufferType = depthBufferType;
		this.useColourBuffer = useColourBuffer;
		this.linearFiltering = linearFiltering;
		this.clampEdge = clampEdge;
		this.alphaChannel = alphaChannel;
		this.antialiased = antialiased;
		this.samples = samples;
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
		return new FBOBuilder(FlounderEngine.getDevices().getDisplay().getWidth(), FlounderEngine.getDevices().getDisplay().getHeight()).fitToScreen(sizeScalar);
	}

	/**
	 * Initializes the FBO.
	 */
	private void initializeFBO() {
		createFBO();

		if (!antialiased) {
			if (useColourBuffer) {
				createTextureAttachment();
			}

			if (depthBufferType == DepthBufferType.RENDER_BUFFER) {
				createDepthBufferAttachment();
			} else if (depthBufferType == DepthBufferType.TEXTURE) {
				createDepthTextureAttachment();
			}
		} else {
			attachMutlisampleColourBuffer();
			createDepthBufferAttachment();
		}

		unbindFrameBuffer();
	}

	private void createFBO() {
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glDrawBuffer(useColourBuffer ? GL_COLOR_ATTACHMENT0 : GL_FALSE);
	}

	private void createTextureAttachment() {
		colourTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colourTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, alphaChannel ? GL_RGBA : GL_RGB, width, height, 0, alphaChannel ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, linearFiltering ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, linearFiltering ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, clampEdge ? GL_CLAMP_TO_EDGE : GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, clampEdge ? GL_CLAMP_TO_EDGE : GL_REPEAT);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colourTexture, 0);
	}

	private void createDepthBufferAttachment() {
		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);

		if (antialiased) {
			glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT24, width, height);
		} else {
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
		}

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
	}

	private void createDepthTextureAttachment() {
		depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
	}

	private void attachMutlisampleColourBuffer() {
		colourBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colourBuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, alphaChannel ? GL_RGBA8 : GL_RGB8, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colourBuffer);
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
		glViewport(0, 0, FlounderEngine.getDevices().getDisplay().getWidth(), FlounderEngine.getDevices().getDisplay().getHeight());
	}

	/**
	 * Renders the colour buffer to the display.
	 */
	public void blitToScreen() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glDrawBuffer(GL_BACK);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
		glBlitFramebuffer(0, 0, width, height, 0, 0, FlounderEngine.getDevices().getDisplay().getWidth(), FlounderEngine.getDevices().getDisplay().getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}

	/**
	 * Blits this FBO to another FBO.
	 *
	 * @param outputFBO The other FBO to blit to.
	 */
	public void resolveFBO(FBO outputFBO) {
		outputFBO.updateSize();
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFBO.frameBuffer);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
		glBlitFramebuffer(0, 0, width, height, 0, 0, outputFBO.width, outputFBO.height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
		unbindFrameBuffer();
	}

	/**
	 * Updates the FBO size if {@code fitToScreen}.
	 */
	private void updateSize() {
		if (fitToScreen) {
			int displayWidth = FlounderEngine.getDevices().getDisplay().getWidth();
			int displayHeight = FlounderEngine.getDevices().getDisplay().getHeight();
			int reverseWidth = (int) (displayWidth * sizeScalar);
			int reverseHeight = (int) (displayHeight * sizeScalar);

			if (displayWidth == 0 || displayHeight == 0) {
				return;
			}

			if (width != reverseWidth || height != reverseHeight) {
				width = (int) (displayWidth * sizeScalar);
				height = (int) (displayHeight * sizeScalar);

				delete();
				FlounderEngine.getLogger().log("Recreating FBO: width: " + width + ", and height: " + height + ".");
				initializeFBO();
			}
		}
	}

	/**
	 * @return The OpenGL colour texture id.
	 */
	public int getColourTexture() {
		return colourTexture;
	}

	/**
	 * @return The OpenGL depth texture id.
	 */
	public int getDepthTexture() {
		return depthTexture;
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
