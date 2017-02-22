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
	private Map<SoundEffect, AudioController> playingSounds;
	private float volume;
	private float pitch;

	/**
	 * Creates a 3D sound emitter.
	 *
	 * @param position The 3D location the sound is comming from.
	 */
	public SoundEmitter(Vector3f position) {
		this.position = position;
		this.playingSounds = new HashMap<>();
		this.volume = 1.0f;
		this.pitch = 1.0f;
	}

	/**
	 * Updates all the sound effects that are currently being played by this emitter, and removes and sound effects that are no longer playing or are out of range of the {@link IAudioListener}.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(float delta) {
		if (playingSounds.keySet().isEmpty()) {
			return;
		}

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
	private boolean updateAudioController(Entry<SoundEffect, AudioController> entry, float delta) {
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
	private boolean isInRange(SoundEffect soundEffect) {
		float disSquared = Vector3f.subtract(FlounderSound.getCameraPosition(), position, null).lengthSquared();
		float range = soundEffect.getRange() * RANGE_THRESHOLD;
		float rangeSquared = range * range;
		return disSquared < rangeSquared;
	}

	/**
	 * @return The position of the emitter in the 3D world.
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * Sets the position of the emitter in the 3D world.
	 *
	 * @param x The x position.
	 * @param y The y position.
	 * @param z The z position.
	 */
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
		playingSounds.values().forEach(audioController -> audioController.setPosition(position));
	}

	/**
	 * @return The volume of the sound emitter.
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * Sets the volume of this emitter.
	 *
	 * @param volume The new volume.
	 */
	public void setVolume(float volume) {
		this.volume = volume;
	}

	/**
	 * @return The pitch of the sound emitter.
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Sets the pitch of this emitter.
	 *
	 * @param pitch The new pitch.
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * Checks if this emitter is currently playing any sounds.
	 *
	 * @return {@code true} if any sounds are currently being played by this emitter.
	 */
	public boolean isInUse() {
		return !playingSounds.isEmpty();
	}

	/**
	 * Plays a sound effect from the position of this emitter in the world. It does this by sending a {@link PlayRequest} to the {@link FlounderSound}.
	 *
	 * @param soundEffect The sound effect to be played from this emitter.
	 */
	public void playSound(SoundEffect soundEffect) {
		if (!soundEffect.getSound().isLoaded() || isPlayingSound(soundEffect) || !isInRange(soundEffect)) {
			return;
		}

		PlayRequest request = PlayRequest.new3dSoundPlayRequest(soundEffect.getSound(), volume, pitch, position, 0.0f, soundEffect.getRange());
		AudioController controller = FlounderSound.play3DSound(request);

		if (controller != null) {
			playingSounds.put(soundEffect, controller);
		}
	}

	/**
	 * Checks if a certain sound effect is being played by this emitter.
	 *
	 * @param sound The sound effect in question.
	 *
	 * @return {@code true} if the sound effect in question is being played by this emitter.
	 */
	public boolean isPlayingSound(SoundEffect sound) {
		return playingSounds.containsKey(sound);
	}

	/**
	 * Stops any sound effects that are currently being played from this emitter.
	 */
	public void silence() {
		playingSounds.values().forEach(AudioController::stop);
		playingSounds.clear();
	}
}
