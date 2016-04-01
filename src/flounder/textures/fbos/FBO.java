package flounder.textures.fbos;

import flounder.devices.*;
import org.lwjgl.opengl.*;

import java.nio.*;

public class FBO {
	private FBOBuilder.DepthBufferType m_depthBufferType;
	private boolean m_useColourBuffer;
	private boolean m_linearFiltering;
	private boolean m_clampEdge;
	private boolean m_alphaChannel;
	private boolean m_antialiased;
	private int m_samples;
	private int m_width;
	private int m_height;
	private boolean m_fitToScreen;

	private int m_frameBuffer;
	private int m_colourTexture;
	private int m_depthTexture;
	private int m_depthBuffer;
	private int m_colourBuffer;

	protected FBO(final int width, final int height, final boolean fitToScreen, final FBOBuilder.DepthBufferType depthBufferType, final boolean useColourBuffer, final boolean linearFiltering, final boolean clampEdge, final boolean alphaChannel, final boolean antialiased, final int samples) {
		m_width = width;
		m_height = height;
		m_fitToScreen = fitToScreen;
		m_depthBufferType = depthBufferType;
		m_useColourBuffer = useColourBuffer;
		m_linearFiltering = linearFiltering;
		m_clampEdge = clampEdge;
		m_alphaChannel = alphaChannel;
		m_antialiased = antialiased;
		m_samples = samples;
		initialiseFBO(depthBufferType, useColourBuffer, linearFiltering, clampEdge, samples);
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

	private void initialiseFBO(final FBOBuilder.DepthBufferType type, final boolean useColourBuffer, final boolean linear, final boolean clamp, final int samples) {
		createFBO(useColourBuffer);

		if (!m_antialiased) {
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
		m_frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, m_frameBuffer);
		GL11.glDrawBuffer(useColourBuffer ? GL30.GL_COLOR_ATTACHMENT0 : GL11.GL_FALSE);
	}

	private void createTextureAttachment(final boolean linear, final boolean clamp) {
		m_colourTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_colourTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, m_alphaChannel ? GL11.GL_RGBA : GL11.GL_RGB, m_width, m_height, 0, m_alphaChannel ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, clamp ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, clamp ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, m_colourTexture, 0);
	}

	private void createDepthBufferAttachment(final int samples) {
		m_depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, m_depthBuffer);

		if (m_antialiased) {
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL14.GL_DEPTH_COMPONENT24, m_width, m_height);
		} else {
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, m_width, m_height);
		}

		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, m_depthBuffer);
	}

	private void createDepthTextureAttachment() {
		m_depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, m_width, m_height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, m_depthTexture, 0);
	}

	private void attachMutlisampleColourBuffer(final int samples) {
		m_colourBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, m_colourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, m_alphaChannel ? GL11.GL_RGBA8 : GL11.GL_RGB8, m_width, m_height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, m_colourBuffer);
	}

	/**
	 * Unbinds the FBO so that other rendering objects can be used.
	 */
	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, ManagerDevices.getDisplay().getWidth(), ManagerDevices.getDisplay().getHeight());
	}

	/**
	 * Binds the FBO so it can be rendered too.
	 */
	public void bindFrameBuffer() {
		updateSize();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, m_frameBuffer);
		GL11.glViewport(0, 0, m_width, m_height);
	}

	private void updateSize() {
		if (m_fitToScreen && (m_width != ManagerDevices.getDisplay().getWidth() || m_height != ManagerDevices.getDisplay().getHeight())) {
			delete();
			m_width = ManagerDevices.getDisplay().getWidth();
			m_height = ManagerDevices.getDisplay().getHeight();
			initialiseFBO(m_depthBufferType, m_useColourBuffer, m_linearFiltering, m_clampEdge, m_samples);
		}
	}

	/**
	 * Deletes the FBO and its attachments.
	 */
	public void delete() {
		GL30.glDeleteFramebuffers(m_frameBuffer);
		GL11.glDeleteTextures(m_colourTexture);
		GL11.glDeleteTextures(m_depthTexture);
		GL30.glDeleteRenderbuffers(m_depthBuffer);
		GL30.glDeleteRenderbuffers(m_colourBuffer);
	}

	/**
	 * Renders the colour buffer to the display.
	 */
	public void blitToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, m_frameBuffer);
		GL30.glBlitFramebuffer(0, 0, m_width, m_height, 0, 0, ManagerDevices.getDisplay().getWidth(), ManagerDevices.getDisplay().getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	}

	/**
	 * Blits this FBO to another FBO.
	 *
	 * @param outputFBO The other FBO to blit to.
	 */
	public void resolveMultisampledFBO(final FBO outputFBO) {
		outputFBO.updateSize();
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, outputFBO.m_frameBuffer);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, m_frameBuffer);
		GL30.glBlitFramebuffer(0, 0, m_width, m_height, 0, 0, outputFBO.m_width, outputFBO.m_height, 16640, GL11.GL_NEAREST);
		unbindFrameBuffer();
	}

	/**
	 * @return The OpenGL colour texture id.
	 */
	public int getColourTexture() {
		return m_colourTexture;
	}

	/**
	 * @return The OpenGL depth texture id.
	 */
	public int getDepthTexture() {
		return m_depthTexture;
	}
}
