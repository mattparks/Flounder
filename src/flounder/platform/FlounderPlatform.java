package flounder.platform;

import flounder.devices.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.profiling.*;

import java.nio.*;

/**
 * A module used for handling networking, servers, clients, and packets.
 */
public class FlounderPlatform extends Module {
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

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		FlounderProfiler.get().add(getTab(), "Platform", getPlatform());
	}

	@Module.MethodReplace
	public Platform getPlatform() {
		return Platform.UNKNOWN;
	}

	/**
	 * Gets the time manager for this platform.
	 *
	 * @return The time manager.
	 */
	@Module.MethodReplace
	public TimingReference getTiming() {
		return null;
	}

	/**
	 * @return The current time time in seconds.
	 */
	@Module.MethodReplace
	public float getTime() {
		return 0.0f;
	}

	/**
	 * Allocates a direct native-ordered bytebuffer with the specified capacity.
	 *
	 * @param capacity The capacity, in bytes.
	 *
	 * @return An ByteBuffer.
	 */
	@Module.MethodReplace
	public ByteBuffer createByteBuffer(int capacity) {
		return null;
	}

	/**
	 * Allocates a direct native-order shortbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in shorts.
	 *
	 * @return An ShortBuffer.
	 */
	@Module.MethodReplace
	public ShortBuffer createShortBuffer(int capacity) {
		return null;
	}

	/**
	 * Allocates a direct native-order charbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in chars.
	 *
	 * @return An CharBuffer.
	 */
	@Module.MethodReplace
	public CharBuffer createCharBuffer(int capacity) {
		return null;
	}

	/**
	 * Allocates a direct native-order intbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in ints.
	 *
	 * @return An IntBuffer.
	 */
	@Module.MethodReplace
	public IntBuffer createIntBuffer(int capacity) {
		return null;
	}

	/**
	 * Allocates a direct native-order longbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in longs.
	 *
	 * @return An LongBuffer.
	 */
	@Module.MethodReplace
	public LongBuffer createLongBuffer(int capacity) {
		return null;
	}

	/**
	 * Allocates a direct native-order floatbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in floats.
	 *
	 * @return An FloatBuffer.
	 */
	@Module.MethodReplace
	public FloatBuffer createFloatBuffer(int capacity) {
		return null;
	}

	/**
	 * Allocates a direct native-order doublebuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in doubles.
	 *
	 * @return An DoubleBuffer.
	 */
	@Module.MethodReplace
	public DoubleBuffer createDoubleBuffer(int capacity) {
		return null;
	}

	/**
	 * Gets the max anisotropy level for textures on this device.
	 *
	 * @return The max anisotropy level.
	 */
	@Module.MethodReplace
	public float getMaxAnisotropy() {
		return 0.0f;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static FlounderPlatform get() {
		return (FlounderPlatform) Framework.getInstance(FlounderPlatform.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Platform";
	}
}
