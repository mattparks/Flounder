package com.flounder.platform;

import com.flounder.devices.*;
import com.flounder.events.*;
import com.flounder.framework.*;
import com.flounder.framework.updater.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.glGetFloat;

/**
 * A module used for handling networking, servers, clients, and packets.
 */
public class FlounderPlatform extends com.flounder.framework.Module {
	/**
	 * Creates a new network manager.
	 */
	public FlounderPlatform() {
		super(FlounderSound.class, FlounderEvents.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_ALWAYS)
	public void update() {
	}

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

	/**
	 * Gets the time manager for this platform.
	 *
	 * @return The time manager.
	 */
	public TimingReference getTiming() {
		return GLFW::glfwGetTime;
	}

	/**
	 * @return The current time time in seconds.
	 */
	public float getTime() {
		return (float) (glfwGetTime() * 1000.0f);
	}

	/**
	 * Allocates a direct native-ordered bytebuffer with the specified capacity.
	 *
	 * @param capacity The capacity, in bytes.
	 *
	 * @return An ByteBuffer.
	 */
	public ByteBuffer createByteBuffer(int capacity) {
		return BufferUtils.createByteBuffer(capacity);
	}

	/**
	 * Allocates a direct native-order shortbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in shorts.
	 *
	 * @return An ShortBuffer.
	 */
	public ShortBuffer createShortBuffer(int capacity) {
		return BufferUtils.createShortBuffer(capacity);
	}

	/**
	 * Allocates a direct native-order charbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in chars.
	 *
	 * @return An CharBuffer.
	 */
	public CharBuffer createCharBuffer(int capacity) {
		return BufferUtils.createCharBuffer(capacity);
	}

	/**
	 * Allocates a direct native-order intbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in ints.
	 *
	 * @return An IntBuffer.
	 */
	public IntBuffer createIntBuffer(int capacity) {
		return BufferUtils.createIntBuffer(capacity);
	}

	/**
	 * Allocates a direct native-order longbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in longs.
	 *
	 * @return An LongBuffer.
	 */
	public LongBuffer createLongBuffer(int capacity) {
		return BufferUtils.createLongBuffer(capacity);
	}

	/**
	 * Allocates a direct native-order floatbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in floats.
	 *
	 * @return An FloatBuffer.
	 */
	public FloatBuffer createFloatBuffer(int capacity) {
		return BufferUtils.createFloatBuffer(capacity);
	}

	/**
	 * Allocates a direct native-order doublebuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in doubles.
	 *
	 * @return An DoubleBuffer.
	 */
	public DoubleBuffer createDoubleBuffer(int capacity) {
		return BufferUtils.createDoubleBuffer(capacity);
	}

	/**
	 * Gets the max anisotropy level for textures on this device.
	 *
	 * @return The max anisotropy level.
	 */
	public float getMaxAnisotropy() {
		return glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@com.flounder.framework.Module.Instance
	public static FlounderPlatform get() {
		return (FlounderPlatform) Framework.get().getModule(FlounderPlatform.class);
	}
}
