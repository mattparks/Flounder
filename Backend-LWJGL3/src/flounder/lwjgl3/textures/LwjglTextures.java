package flounder.lwjgl3.textures;

import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;
import flounder.textures.*;

import java.io.*;
import java.nio.*;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

@Module.ModuleOverride
public class LwjglTextures extends FlounderTextures {
	private float anisotropyLevel;

	public LwjglTextures(float anisotropyLevel) {
		super();
		this.anisotropyLevel = anisotropyLevel;
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
	public void loadTexture(TextureBuilder builder, TextureObject object) {
		if (builder.getFile() != null) {
			int textureID = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, textureID);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, object.getWidth(), object.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, object.getBuffer());

			if (builder.isMipmap()) {
				glGenerateMipmap(GL_TEXTURE_2D);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

				if (builder.isAnisotropic()) {
					glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);
					glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, FlounderTextures.get().getAnisotropyLevel());
				}
			} else if (builder.isNearest()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			} else {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			}

			if (builder.isClampEdges()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			} else if (builder.isClampToBorder()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
				FloatBuffer buffer = FlounderPlatform.get().createFloatBuffer(4);
				builder.getBorderColour().store(buffer);
				buffer.flip();
				glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, buffer);
			} else {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			}

			object.loadGL(textureID, GL_TEXTURE_2D);
		} else if (builder.getCubemap() != null) {
			int textureID = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

			for (int i = 0; i < builder.getCubemap().length; i++) {
				int width = 0;
				int height = 0;
				boolean hasAlpha = false;
				ByteBuffer buffer = null;

				try {
					InputStream in = builder.getCubemap()[i].getInputStream();
					TextureDecoder decoder = new TextureDecoder(in);
					width = decoder.getWidth();
					height = decoder.getHeight();
					hasAlpha = decoder.hasAlpha();
					buffer = ByteBuffer.allocateDirect(4 * width * height);
					decoder.decode(buffer, width * 4, TextureDecoder.Format.BGRA);
					buffer.flip();
					in.close();
				} catch (Exception e) {
					FlounderLogger.get().error("Tried to load texture '" + builder.getCubemap()[i] + "', didn't work");
					FlounderLogger.get().exception(e);
					System.exit(-1);
				}

				object.setHasAlpha(hasAlpha || object.hasAlpha());
				glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			}

			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
			object.loadGL(textureID, GL_TEXTURE_CUBE_MAP);
		}
	}

	@Override
	public void deleteTexture(int textureID) {
		glDeleteTextures(textureID);
	}

	@Override
	public float getAnisotropyLevel() {
		return anisotropyLevel;
	}

	@Override
	public void setAnisotropyLevel(float anisotropyLevel) {
		this.anisotropyLevel = anisotropyLevel;
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
