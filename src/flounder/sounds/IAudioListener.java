package flounder.sounds;

import flounder.maths.vectors.*;

/**
 * An audio listener in the 3D world. The volume of sound effects depends on the position of the sound emitter and the position of the listener.
 */
@FunctionalInterface
public interface IAudioListener {
	/**
	 * @return The 3D position of the listener.
	 */
	Vector3f getPosition();
}
