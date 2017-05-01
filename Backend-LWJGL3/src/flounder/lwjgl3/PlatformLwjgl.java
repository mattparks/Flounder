package flounder.lwjgl3;

import flounder.framework.updater.*;
import flounder.platform.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;

import java.nio.*;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;

public class PlatformLwjgl extends IPlatform {
	public PlatformLwjgl() {
		super();
	}

	@Override
	public Platform getPlatform() {
		final String OS = System.getProperty("os.name").toLowerCase();
		final String ARCH = System.getProperty("os.arch").toLowerCase();

		boolean isWindows = OS.contains("windows");
		boolean isLinux = OS.contains("linux");
		boolean isMac = OS.contains("mac");
		boolean is64Bit = ARCH.equals("amd64") || ARCH.equals("x86_64");

		Platform platform = Platform.UNKNOWN;

		if (isWindows) {
			platform = is64Bit ? Platform.WINDOWS_64 : Platform.WINDOWS_32;
		}

		if (isLinux) {
			platform = is64Bit ? Platform.LINUX_64 : Platform.UNKNOWN;
		}

		if (isMac) {
			platform = Platform.MACOS;
		}

		return platform;
	}

	@Override
	public TimingReference getTiming() {
		return GLFW::glfwGetTime;
	}

	@Override
	public ByteBuffer createByteBuffer(int capacity) {
		return BufferUtils.createByteBuffer(capacity);
	}

	@Override
	public ShortBuffer createShortBuffer(int capacity) {
		return BufferUtils.createShortBuffer(capacity);
	}

	@Override
	public CharBuffer createCharBuffer(int capacity) {
		return BufferUtils.createCharBuffer(capacity);
	}

	@Override
	public IntBuffer createIntBuffer(int capacity) {
		return BufferUtils.createIntBuffer(capacity);
	}

	@Override
	public LongBuffer createLongBuffer(int capacity) {
		return BufferUtils.createLongBuffer(capacity);
	}

	@Override
	public FloatBuffer createFloatBuffer(int capacity) {
		return BufferUtils.createFloatBuffer(capacity);
	}

	@Override
	public DoubleBuffer createDoubleBuffer(int capacity) {
		return BufferUtils.createDoubleBuffer(capacity);
	}

	@Override
	public float getMaxAnisotropy() {
		return glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
	}
}
