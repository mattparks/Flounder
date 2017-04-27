package flounder.platform;

import flounder.framework.*;
import flounder.logger.*;
import flounder.lwjgl3.PlatformLWJGL3;
import flounder.profiling.*;

import java.nio.*;

/**
 * A module used for handling networking, servers, clients, and packets.
 */
public class FlounderPlatform extends Module {
	private static final FlounderPlatform INSTANCE = new FlounderPlatform();
	public static final String PROFILE_TAB_NAME = "Platform";

	private IPlatform platform;

	/**
	 * Creates a new network manager.
	 */
	public FlounderPlatform() {
		super(ModuleUpdate.UPDATE_ALWAYS, PROFILE_TAB_NAME);
		this.platform = new PlatformLWJGL3(); // TODO
	}

	@Override
	public void init() {
		Framework.getUpdater().setTiming(platform.getTiming());
		FlounderLogger.log(platform.getTiming());
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Platform", platform);
	}

	public static Platform getPlatform() {
		return INSTANCE.platform.getPlatform();
	}

	public static ByteBuffer createByteBuffer(int capacity) {
		return INSTANCE.platform.createByteBuffer(capacity);
	}

	public static ShortBuffer createShortBuffer(int capacity) {
		return INSTANCE.platform.createShortBuffer(capacity);
	}

	public static CharBuffer createCharBuffer(int capacity) {
		return INSTANCE.platform.createCharBuffer(capacity);
	}

	public static IntBuffer createIntBuffer(int capacity) {
		return INSTANCE.platform.createIntBuffer(capacity);
	}

	public static LongBuffer createLongBuffer(int capacity) {
		return INSTANCE.platform.createLongBuffer(capacity);
	}

	public static FloatBuffer createFloatBuffer(int capacity) {
		return INSTANCE.platform.createFloatBuffer(capacity);
	}

	public static DoubleBuffer createDoubleBuffer(int capacity) {
		return INSTANCE.platform.createDoubleBuffer(capacity);
	}

	public static float getMaxAnisotropy() {
		return INSTANCE.platform.getMaxAnisotropy();
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
