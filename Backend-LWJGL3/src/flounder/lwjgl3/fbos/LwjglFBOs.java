package flounder.lwjgl3.fbos;

import flounder.devices.*;
import flounder.fbos.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;

import java.nio.*;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

@Module.ModuleOverride
public class LwjglFBOs extends FlounderFBOs {
	public LwjglFBOs() {
		super();
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
	public void initializeFBO(FBO fbo) {
		createFBO(fbo);
		limitFBOSize(fbo);

		if (!fbo.isAntialiased()) {
			if (fbo.isUseColourBuffer()) {
				for (int i = 0; i < fbo.getAttachments(); i++) {
					createTextureAttachment(fbo, GL_COLOR_ATTACHMENT0 + i);
				}
			}

			if (fbo.getDepthBufferType() == DepthBufferType.RENDER_BUFFER) {
				createDepthBufferAttachment(fbo);
			} else if (fbo.getDepthBufferType() == DepthBufferType.TEXTURE) {
				createDepthTextureAttachment(fbo);
			}
		} else {
			for (int i = 0; i < fbo.getAttachments(); i++) {
				attachMultisampleColourBuffer(fbo, GL_COLOR_ATTACHMENT0 + i);
			}

			createDepthBufferAttachment(fbo);
		}

		unbindFrameBuffer();
	}

	@Override
	public void createFBO(FBO fbo) {
		fbo.setFrameBuffer(glGenFramebuffers());
		glBindFramebuffer(GL_FRAMEBUFFER, fbo.getFrameBuffer());

		if (fbo.isUseColourBuffer()) {
			determineDrawBuffers(fbo);
		} else {
			glDrawBuffer(GL_FALSE);
		}
	}

	@Override
	public void determineDrawBuffers(FBO fbo) {
		IntBuffer drawBuffers = FlounderPlatform.get().createIntBuffer(fbo.getAttachments());

		for (int i = 0; i < fbo.getAttachments(); i++) {
			drawBuffers.put(GL_COLOR_ATTACHMENT0 + i);
		}

		drawBuffers.flip();
		glDrawBuffers(drawBuffers);
	}

	@Override
	public void createTextureAttachment(FBO fbo, int attachment) {
		fbo.getColourTexture()[attachment - GL_COLOR_ATTACHMENT0] = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, fbo.getColourTexture()[attachment - GL_COLOR_ATTACHMENT0]);
		glTexImage2D(GL_TEXTURE_2D, 0, fbo.isAlphaChannel() ? GL_RGBA : GL_RGB, fbo.getWidth(), fbo.getHeight(), 0, fbo.isAlphaChannel() ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, fbo.isLinearFiltering() ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, fbo.isLinearFiltering() ? GL_LINEAR : GL_NEAREST);

		if (fbo.isWrapTextures()) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, fbo.isClampEdge() ? GL_CLAMP_TO_EDGE : GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, fbo.isClampEdge() ? GL_CLAMP_TO_EDGE : GL_REPEAT);
		}

		glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, fbo.getColourTexture()[attachment - GL_COLOR_ATTACHMENT0], 0);
	}

	@Override
	public void createDepthBufferAttachment(FBO fbo) {
		fbo.setDepthBuffer(glGenRenderbuffers());
		glBindRenderbuffer(GL_RENDERBUFFER, fbo.getDepthBuffer());

		if (fbo.isAntialiased()) {
			glRenderbufferStorageMultisample(GL_RENDERBUFFER, fbo.getSamples(), GL_DEPTH_COMPONENT24, fbo.getWidth(), fbo.getHeight());
		} else {
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, fbo.getWidth(), fbo.getHeight());
		}

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, fbo.getDepthBuffer());
	}

	@Override
	public void createDepthTextureAttachment(FBO fbo) {
		fbo.setDepthTexture(glGenTextures());
		glBindTexture(GL_TEXTURE_2D, fbo.getDepthTexture());
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, fbo.getWidth(), fbo.getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, fbo.getDepthTexture(), 0);
	}

	@Override
	public void attachMultisampleColourBuffer(FBO fbo, int attachment) {
		fbo.getColourBuffer()[attachment - GL_COLOR_ATTACHMENT0] = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, fbo.getColourBuffer()[attachment - GL_COLOR_ATTACHMENT0]);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, fbo.getSamples(), fbo.isAlphaChannel() ? GL_RGBA8 : GL_RGB8, fbo.getWidth(), fbo.getHeight());
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, fbo.getColourBuffer()[attachment - GL_COLOR_ATTACHMENT0]);
	}

	@Override
	public void bindFrameBuffer(FBO fbo) {
		fbo.updateSize();
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo.getFrameBuffer());
		glViewport(0, 0, fbo.getWidth(), fbo.getHeight());
	}

	@Override
	public void unbindFrameBuffer() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, FlounderDisplay.get().getWidth(), FlounderDisplay.get().getHeight());
	}

	@Override
	public void blitToScreen(FBO fbo) {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glDrawBuffer(GL_BACK);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo.getFrameBuffer());
		glBlitFramebuffer(0, 0, fbo.getWidth(), fbo.getHeight(), 0, 0, FlounderDisplay.get().getWidth(), FlounderDisplay.get().getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}

	@Override
	public void resolveFBO(FBO fbo, FBO outputFBO) {
		if (fbo.getAttachments() != outputFBO.getAttachments() && fbo.isHasGivenResolveError() != outputFBO.isHasGivenResolveError()) {
			fbo.setHasGivenResolveError(true);
			outputFBO.setHasGivenResolveError(true);
			FlounderLogger.get().log("Warning, resolving two FBO's (" + fbo + ", " + outputFBO + ") with different attachment sizes, be warned this may not work properly instead use resolveFBO(int readBuffer, int drawBuffer, FBO outputFBO).");
		}

		for (int a = 0; a < fbo.getAttachments(); a++) {
			resolveFBO(fbo, a, a, outputFBO);
		}
	}

	@Override
	public void resolveFBO(FBO fbo, int readBuffer, int drawBuffer, FBO outputFBO) {
		outputFBO.updateSize();
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFBO.getFrameBuffer());
		glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo.getFrameBuffer());

		glReadBuffer(GL_COLOR_ATTACHMENT0 + readBuffer);
		glDrawBuffer(GL_COLOR_ATTACHMENT0 + drawBuffer);
		glBlitFramebuffer(0, 0, fbo.getWidth(), fbo.getHeight(), 0, 0, outputFBO.getWidth(), outputFBO.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);

		unbindFrameBuffer();
	}

	@Override
	public void limitFBOSize(FBO fbo) {
		fbo.setWidth(Math.min(FlounderFBOs.get().getMaxFBOSize(), fbo.getWidth()));
		fbo.setHeight(Math.min(FlounderFBOs.get().getMaxFBOSize(), fbo.getHeight()));
	}

	@Override
	public void delete(FBO fbo) {
		glDeleteFramebuffers(fbo.getFrameBuffer());
		glDeleteTextures(fbo.getColourTexture());
		glDeleteTextures(fbo.getDepthTexture());
		glDeleteRenderbuffers(fbo.getDepthBuffer());
		glDeleteRenderbuffers(fbo.getColourBuffer());
	}

	@Override
	public int getMaxFBOSize() {
		return glGetInteger(GL_MAX_RENDERBUFFER_SIZE_EXT);
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
