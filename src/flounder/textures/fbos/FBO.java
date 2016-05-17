package flounder.textures.fbos;

import flounder.devices.*;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

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
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glDrawBuffer(useColourBuffer ? GL_COLOR_ATTACHMENT0 : GL_FALSE);
	}

	private void createTextureAttachment(final boolean linear, final boolean clamp) {
		colourTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colourTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, alphaChannel ? GL_RGBA : GL_RGB, width, height, 0, alphaChannel ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, linear ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, linear ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, clamp ? GL_CLAMP_TO_EDGE : GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, clamp ? GL_CLAMP_TO_EDGE : GL_REPEAT);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colourTexture, 0);
	}

	private void createDepthBufferAttachment(final int samples) {
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

	private void attachMutlisampleColourBuffer(final int samples) {
		colourBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colourBuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, alphaChannel ? GL_RGBA8 : GL_RGB8, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colourBuffer);
	}

	/**
	 * Unbinds the FBO so that other rendering objects can be used.
	 */
	public void unbindFrameBuffer() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, ManagerDevices.getDisplay().getWidth(), ManagerDevices.getDisplay().getHeight());
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
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
		glViewport(0, 0, width, height);
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
		glDeleteFramebuffers(frameBuffer);
		glDeleteTextures(colourTexture);
		glDeleteTextures(depthTexture);
		glDeleteRenderbuffers(depthBuffer);
		glDeleteRenderbuffers(colourBuffer);
	}

	/**
	 * Renders the colour buffer to the display.
	 */
	public void blitToScreen() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glDrawBuffer(GL_BACK);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
		glBlitFramebuffer(0, 0, width, height, 0, 0, ManagerDevices.getDisplay().getWidth(), ManagerDevices.getDisplay().getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}

	/**
	 * Blits this FBO to another FBO.
	 *
	 * @param outputFBO The other FBO to blit to.
	 */
	public void resolveMultisampledFBO(final FBO outputFBO) {
		outputFBO.updateSize();
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFBO.frameBuffer);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
		glBlitFramebuffer(0, 0, width, height, 0, 0, outputFBO.width, outputFBO.height, 16640, GL_NEAREST);
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
