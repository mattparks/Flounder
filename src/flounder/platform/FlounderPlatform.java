package flounder.platform;

import flounder.devices.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.profiling.*;

import java.nio.*;

/**
 * A module used for handling networking, servers, clients, and packets.
 */
public class FlounderPlatform extends Module {
	private IPlatform platform;

	/**
	 * Creates a new network manager.
	 */
	public FlounderPlatform() {
		super(FlounderSound.class, FlounderEvents.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		update();
		if (platform != null) {
			Framework.getUpdater().setTiming(platform.getTiming());
			//	FlounderLogger.get().log(platform.getTiming());
			platform.init();
		}
	}

	@Handler.Function(Handler.FLAG_UPDATE_ALWAYS)
	public void update() {
		// Gets a new platform, if available.
		IPlatform newPlatform = (IPlatform) getExtension(platform, IPlatform.class, true);

		// If there is a new player, disable the old one and start to use the new one.
		if (newPlatform != null) {
			if (platform != null) {
				platform.setInitialized(false);
			}

			if (!newPlatform.isInitialized()) {
				//	newPlatform.init();
				newPlatform.setInitialized(true);
			}

			platform = newPlatform;
		}
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		FlounderProfiler.get().add(getTab(), "Platform", platform);
	}

	public Platform getPlatform() {
		return this.platform.getPlatform();
	}

	public ByteBuffer createByteBuffer(int capacity) {
		return this.platform.createByteBuffer(capacity);
	}

	public ShortBuffer createShortBuffer(int capacity) {
		return this.platform.createShortBuffer(capacity);
	}

	public CharBuffer createCharBuffer(int capacity) {
		return this.platform.createCharBuffer(capacity);
	}

	public IntBuffer createIntBuffer(int capacity) {
		return this.platform.createIntBuffer(capacity);
	}

	public LongBuffer createLongBuffer(int capacity) {
		return this.platform.createLongBuffer(capacity);
	}

	public FloatBuffer createFloatBuffer(int capacity) {
		return this.platform.createFloatBuffer(capacity);
	}

	public DoubleBuffer createDoubleBuffer(int capacity) {
		return this.platform.createDoubleBuffer(capacity);
	}

	public float getMaxAnisotropy() {
		return this.platform.getMaxAnisotropy();
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
