package flounder.sounds;

import flounder.maths.vectors.*;

/**
 * Sound sources are what play the sounds in OpenAL. This class represents one source, capable of playing one sound at a time.
 * This object also determines how the sound is played (volume, position, etc).
 */
public abstract class SoundSource {
	protected float volume;
	protected float pitch;
	protected boolean active;

	protected AudioController currentController;

	/**
	 * Creates a new sound source and gives it some default settings.
	 */
	protected SoundSource() {
		volume = 1.0f;
		pitch = 1.0f;
		active = false;
	}

	/**
	 * @param radius The range of the sound. Outside this range the sound can't be heard. Between the position of the source and the outer radius the volume of the sound decreases linearly.
	 */
	protected abstract void setRange(float radius);

	/**
	 * Indicates that the sound has no range, and will always be played at full volume regardless of where the listener and source are.
	 */
	protected abstract void setUndiminishing();

	/**
	 * Sets the inner and outer ranges for the source. Inside the inner range sounds are heard at full volume.
	 * Between the inner and outer radiuses the volume of sounds decreases linearly.
	 *
	 * @param primaryRadius The inner range.
	 * @param secondaryRadius The outer range.
	 */
	protected abstract void setRanges(float primaryRadius, float secondaryRadius);

	/**
	 * @return The source's volume.
	 */
	protected abstract float getVolume();

	/**
	 * Sets the volume of the source. Any sounds played on this source will be played at this volume.
	 *
	 * @param newVolume The new volume.
	 */
	protected abstract void setVolume(float newVolume);

	/**
	 * @return The source's pitch.
	 */
	protected abstract float getPitch();

	/**
	 * Sets the pitch of the source. Any sounds played on this source will be played at this pitch.
	 *
	 * @param newPitch The new pitch.
	 */
	protected abstract void setPitch(float newPitch);

	/**
	 * @param position The 3D position of the source in the world.
	 */
	protected abstract void setPosition(Vector3f position);

	/**
	 * @param loop Whether the source should play sounds on loop or not.
	 */
	protected abstract void loop(boolean loop);

	/**
	 * Plays a sound and returns an {@link AudioController} which allows the settings of the source to be changed while the sound is playing.
	 * Streaming is automatically handled if the sound file is large.
	 *
	 * @param sound The sound to be played.
	 *
	 * @return The controller for the playing of this sound.
	 */
	protected abstract AudioController playSound(Sound sound);

	/**
	 * Stops the source playing the current sound.
	 */
	protected abstract void stop();

	/**
	 * Indicates that the source has finished playing the current sound. This notifies the current controller as well as removing any buffers that were queued to this source.
	 */
	protected abstract void setInactive();

	/**
	 * Removes the top buffer that has already been played from the queue (for use when streaming).
	 */
	protected abstract void unqueue();

	/**
	 * @return The number of buffers in the queue that have already been played (for use when streaming).
	 */
	protected abstract int getFinishedBuffersCount();

	/**
	 * @return {@code true} if the source is currently playing a sound.
	 */
	protected abstract boolean isPlaying();

	/**
	 * Queues up a buffer to be played by this source as soon as it has finished playing the current buffer (for use when streaming).
	 *
	 * @param buffer The buffer to be queued.
	 */
	protected abstract void queue(int buffer);

	/**
	 * Pauses the sound source.
	 */
	protected abstract void pause();

	/**
	 * Unpauses the currently paused sound source.
	 */
	protected abstract void unpause();

	/**
	 * Deletes the source.
	 */
	protected abstract void delete();
}
