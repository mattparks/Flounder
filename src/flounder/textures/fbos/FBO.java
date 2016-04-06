package flounder.textures.fbos;

import flounder.devices.*;
import org.lwjgl.opengl.*;

import java.nio.*;

public class FBO {
	private FBOBuilder.DepthBufferType depthBufferType;
	private boolean useColourBuffer;
	private boolean linearFiltering;
	private boolean clampEdge;
	private boolean alphaChannel;
	private boolean antialiased;
	private int samples;
	private int width;
	private int height;
	private boolean fitToScreen;

	private int frameBuffer;
	private int colourTexture;
	private int depthTexture;
	private int depthBuffer;
	private int colourBuffer;

	protected FBO(final int width, final int height, final boolean fitToScreen, final FBOBuilder.DepthBufferType depthBufferType, final boolean useColourBuffer, final boolean linearFiltering, final boolean clampEdge, final boolean alphaChannel, final boolean antialiased, final int samples) {
		this.width = width;
		this.height = height;
		this.fitToScreen = fitToScreen;
		this.depthBufferType = depthBufferType;
		this.useColourBuffer = useColourBuffer;
		this.linearFiltering = linearFiltering;
		this.clampEdge = clampEdge;
		this.alphaChannel = alphaChannel;
		this.antialiased = antialiased;
		this.samples = samples;
		initialiseFBO(depthBufferType, useColourBuffer, linearFiltering, clampEdge, samples);
	}

	private void initialiseFBO(final FBOBuilder.DepthBufferType type, final boolean useColourBuffer, final boolean linear, final boolean clamp, final int samples) {
		createFBO(useColourBuffer);

		if (!antialiased) {
			if (useColourBuffer) {
				createTextureAttachment(linear, clamp);
			}

			if (type == FBOBuilder.DepthBufferType.RENDER_BUFFER) {
				createDepthBufferAttachment(samples);
			} else if (type == FBOBuilder.DepthBufferType.TEXTURE) {
				createDepthTextureAttachment();
			}
		} else {
			attachMutlisampleColourBuffer(samples);
			createDepthBufferAttachment(samples);
		}

		unbindFrameBuffer();
	}

	private void createFBO(final boolean useColourBuffer) {
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glDrawBuffer(useColourBuffer ? GL30.GL_COLOR_ATTACHMENT0 : GL11.GL_FALSE);
	}

	private void createTextureAttachment(final boolean linear, final boolean clamp) {
		colourTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, alphaChannel ? GL11.GL_RGBA : GL11.GL_RGB, width, height, 0, alphaChannel ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, clamp ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, clamp ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture, 0);
	}

	private void createDepthBufferAttachment(final int samples) {
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);

		if (antialiased) {
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL14.GL_DEPTH_COMPONENT24, width, height);
		} else {
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
		}

		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
	}

	private void createDepthTextureAttachment() {
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}

	private void attachMutlisampleColourBuffer(final int samples) {
		colourBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, alphaChannel ? GL11.GL_RGBA8 : GL11.GL_RGB8, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, colourBuffer);
	}

	/**
	 * Unbinds the FBO so that other rendering objects can be used.
	 */
	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, ManagerDevices.getDisplay().getWidth(), ManagerDevices.getDisplay().getHeight());
	}

	/**
	 * Creates a new FBO Builder.
	 *
	 * @param width The initial width for the new FBO.
	 * @param height The initial height for the new FBO.
	 *
	 * @return A new FBO Builder.
	 */
	public static FBOBuilder newFBO(final int width, final int height) {
		return new FBOBuilder(width, height);
	}

	/**
	 * Binds the FBO so it can be rendered too.
	 */
	public void bindFrameBuffer() {
		updateSize();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}

	private void updateSize() {
		if (fitToScreen && (width != ManagerDevices.getDisplay().getWidth() || height != ManagerDevices.getDisplay().getHeight())) {
			delete();
			width = ManagerDevices.getDisplay().getWidth();
			height = ManagerDevices.getDisplay().getHeight();
			initialiseFBO(depthBufferType, useColourBuffer, linearFiltering, clampEdge, samples);
		}
	}

	/**
	 * Deletes the FBO and its attachments.
	 */
	public void delete() {
		GL30.glDeleteFramebuffers(frameBuffer);
		GL11.glDeleteTextures(colourTexture);
		GL11.glDeleteTextures(depthTexture);
		GL30.glDeleteRenderbuffers(depthBuffer);
		GL30.glDeleteRenderbuffers(colourBuffer);
	}

	/**
	 * Renders the colour buffer to the display.
	 */
	public void blitToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, ManagerDevices.getDisplay().getWidth(), ManagerDevices.getDisplay().getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	}

	/**
	 * Blits this FBO to another FBO.
	 *
	 * @param outputFBO The other FBO to blit to.
	 */
	public void resolveMultisampledFBO(final FBO outputFBO) {
		outputFBO.updateSize();
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, outputFBO.frameBuffer);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, outputFBO.width, outputFBO.height, 16640, GL11.GL_NEAREST);
		unbindFrameBuffer();
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
}
