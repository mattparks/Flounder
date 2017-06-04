package flounder.lwjgl3;

import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.lwjgl3.devices.*;
import flounder.lwjgl3.fbos.*;
import flounder.lwjgl3.helpers.*;
import flounder.lwjgl3.loaders.*;
import flounder.lwjgl3.shaders.*;
import flounder.lwjgl3.textures.*;
import flounder.platform.*;
import flounder.resources.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;

@Module.ModuleOverride
public class PlatformLwjgl extends FlounderPlatform {
	public PlatformLwjgl(int width, int height, String title, MyFile[] icons, boolean vsync, boolean antialiasing, int samples, boolean fullscreen, boolean hiddenDisplay, boolean wireframe, float anisotropyLevel) {
		super();
		Framework.get().addOverrides(
				new LwjglDisplay(3, getPlatform().equals(Platform.MACOS) ? 3 : 2, width, height, title, icons, vsync, antialiasing, samples, fullscreen, hiddenDisplay),
				new LwjglJoysicks(),
				new LwjglKeyboard(),
				new LwjglMouse(),
				new LwjglSound(),
				new LwjglOpenGL(wireframe),
				new LwjglFBOs(),
				new LwjglLoaders(),
				new LwjglShaders(),
				new LwjglTextures(anisotropyLevel)
		);
		Framework.get().getUpdater().setTiming(GLFW::glfwGetTime);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		super.init();
	}

	@Handler.Function(Handler.FLAG_UPDATE_ALWAYS)
	public void update() {
		super.update();
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
	public float getTime() {
		return (float) (glfwGetTime() * 1000.0f);
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

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();
	}
}
