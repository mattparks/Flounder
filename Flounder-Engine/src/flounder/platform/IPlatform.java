package flounder.platform;

import flounder.framework.updater.*;

import java.nio.*;

/**
 * An implementation for objects in a platform.
 */
public interface IPlatform {
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	Platform getPlatform();

	/**
	 * Gets the time manager for this platform.
	 *
	 * @return The time manager.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	TimingReference getTiming();

	/**
	 * Allocates a direct native-ordered bytebuffer with the specified capacity.
	 *
	 * @param capacity The capacity, in bytes.
	 *
	 * @return An ByteBuffer.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	ByteBuffer createByteBuffer(int capacity);

	/**
	 * Allocates a direct native-order shortbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in shorts.
	 *
	 * @return An ShortBuffer.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	ShortBuffer createShortBuffer(int capacity);

	/**
	 * Allocates a direct native-order charbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in chars.
	 *
	 * @return An CharBuffer.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	CharBuffer createCharBuffer(int capacity);

	/**
	 * Allocates a direct native-order intbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in ints.
	 *
	 * @return An IntBuffer.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	IntBuffer createIntBuffer(int capacity);

	/**
	 * Allocates a direct native-order longbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in longs.
	 *
	 * @return An LongBuffer.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	LongBuffer createLongBuffer(int capacity);

	/**
	 * Allocates a direct native-order floatbuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in floats.
	 *
	 * @return An FloatBuffer.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	FloatBuffer createFloatBuffer(int capacity);

	/**
	 * Allocates a direct native-order doublebuffer with the specified number of elements.
	 *
	 * @param capacity The capacity, in doubles.
	 *
	 * @return An DoubleBuffer.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	DoubleBuffer createDoubleBuffer(int capacity);

	/**
	 * Gets the max anisotropy level for textures on this device.
	 *
	 * @return The max anisotropy level.
	 */
	@PlatformAndroid
	@PlatformHTML5
	@PlatformLWJGL3
	float getMaxAnisotropy();
}
