package flounder.sounds;

import flounder.devices.*;
import flounder.maths.vectors.*;

import java.util.*;
import java.util.Map.*;

/**
 * An emitter of sound effects in the 3D world. The volume of sounds emitted from a SoundEmitter depends on how close the {@link IAudioListener} is to the emitter in the 3D world.
 */
public class SoundEmitter {
	private static final float RANGE_THRESHOLD = 1.2f;

	private Vector3f position;
	private float volume;

	private Map<SoundEffect, AudioController> playingSounds;

	public SoundEmitter(final Vector3f position) {
		this.position = position;
		volume = 1;
		playingSounds = new HashMap<>();
	}

	/**
	 * Updates all the sound effects that are currently being played by this emitter, and removes and sound effects that are no longer playing or are out of range of the {@link IAudioListener}.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(final float delta) {
		Iterator<Entry<SoundEffect, AudioController>> iterator = playingSounds.entrySet().iterator();

		while (iterator.hasNext()) {
			boolean stillPlaying = updateAudioController(iterator.next(), delta);

			if (!stillPlaying) {
				iterator.remove();
			}
		}
	}

	/**
	 * Updates an audio controller of a sound that is currently being played by this emitter. Any sounds being played that are out of range of the listener are stopped via their controller.
	 *
	 * @param entry A sound effect and its corresponding controller.
	 * @param delta The time in seconds since the last frame.
	 *
	 * @return {@code true} if the sound is still being played.
	 */
	private boolean updateAudioController(final Entry<SoundEffect, AudioController> entry, final float delta) {
		AudioController controller = entry.getValue();

		if (!isInRange(entry.getKey())) {
			controller.stop();
		}

		return controller.update(delta);
	}

	/**
	 * Checks if a certain sound effect being played by this emitter would be heard at all by the {@link IAudioListener}.
	 *
	 * @param soundEffect The sound effect in question.
	 *
	 * @return {@code true} if the sound effect would be heard by the listener when played from this emitter.
	 */
	private boolean isInRange(final SoundEffect soundEffect) {
		float disSquared = Vector3f.subtract(ManagerDevices.getSound().getCameraPosition(), position, null).lengthSquared();
		float range = soundEffect.getRange() * RANGE_THRESHOLD;
		float rangeSquared = range * range;
		return disSquared < rangeSquared;
	}

	/**
	 * @return The position of the emitter in the 3D world.
	 */
	public final Vector3f getPosition() {
		return position;
	}

	/**
	 * Sets the position of the emitter in the 3D world.
	 *
	 * @param x The x position.
	 * @param y The y position.
	 * @param z The z position.
	 */
	public void setPosition(final float x, final float y, final float z) {
		position.set(x, y, z);

		for (AudioController controller : playingSounds.values()) {
			controller.setPosition(position);
		}
	}

	/**
	 * @return The volume of the sound emitter.
	 */
	public final float getVolume() {
		return volume;
	}

	/**
	 * Sets the volume of this emitter.
	 *
	 * @param volume The new volume.
	 */
	public void setVolume(final float volume) {
		this.volume = volume;
	}

	/**
	 * Checks if this emitter is currently playing any sounds.
	 *
	 * @return {@code true} if any sounds are currently being played by this emitter.
	 */
	public final boolean isInUse() {
		return !playingSounds.isEmpty();
	}

	/**
	 * Checks if a certain sound effect is being played by this emitter.
	 *
	 * @param sound The sound effect in question.
	 *
	 * @return {@code true} if the sound effect in question is being played by this emitter.
	 */
	public final boolean isPlayingSound(final SoundEffect sound) {
		return playingSounds.containsKey(sound);
	}

	/**
	 * Plays a sound effect from the position of this emitter in the world. It does this by sending a {@link PlayRequest} to the {@link DeviceSound}.
	 *
	 * @param soundEffect The sound effect to be played from this emitter.
	 */
	public void playSound(final SoundEffect soundEffect) {
		if (!soundEffect.getSound().isLoaded() || isPlayingSound(soundEffect) || !isInRange(soundEffect)) {
			return;
		}

		PlayRequest request = PlayRequest.new3dSoundPlayRequest(soundEffect.getSound(), volume, position, 0, soundEffect.getRange());
		AudioController controller = ManagerDevices.getSound().play3DSound(request);

		if (controller != null) {
			playingSounds.put(soundEffect, controller);
		}
	}

	/**
	 * Stops any sound effects that are currently being played from this emitter.
	 */
	public void silence() {
		for (AudioController controller : playingSounds.values()) {
			controller.stop();
		}

		playingSounds.clear();
	}
}
