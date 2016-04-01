package flounder.sounds;

import flounder.maths.vectors.*;
import org.lwjgl.openal.*;

/**
 * Sound sources are what play the sounds in OpenAL. This class represents one source, capable of playing one sound at a time.
 * This object also determines how the sound is played (volume, position, etc).
 */
public class SoundSource {
	private int sourceID;
	private float volume;
	private boolean active;

	private AudioController currentController;

	/**
	 * Creates a new sound source and gives it some default settings.
	 */
	protected SoundSource() {
		sourceID = createSource();
		volume = 1;
		active = false;
		AL10.alSource3f(sourceID, AL10.AL_POSITION, 0, 0, 0);
		AL10.alSource3f(sourceID, AL10.AL_VELOCITY, 1, 0, 0);
		AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, 0);
		AL10.alSourcef(sourceID, AL10.AL_GAIN, volume);
	}

	/**
	 * @return The ID of the newly created OpenAL source.
	 */
	private static final int createSource() {
		int sourceID = AL10.alGenSources();

		if (AL10.alGetError() != AL10.AL_NO_ERROR) {
			System.err.println("Problem creating sound source!");
		}

		return sourceID;
	}

	/**
	 * @param radius - the range of the sound. Outside this range the sound can't be heard. Between the position of the source and the outer radius the volume of the sound decreases linearly.
	 */
	protected void setRange(final float radius) {
		AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, 1);
		AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, 1);
		AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, radius);
	}

	/**
	 * Indicates that the sound has no range, and will always be played at full volume regardless of where the listener and source are.
	 */
	protected void setUndiminishing() {
		AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, 0);
	}

	/**
	 * Sets the inner and outer ranges for the source. Inside the inner range sounds are heard at full volume.
	 * Between the inner and outer radiuses the volume of sounds decreases linearly.
	 *
	 * @param primaryRadius The inner range.
	 * @param secondaryRadius The outer range.
	 */
	protected void setRanges(final float primaryRadius, final float secondaryRadius) {
		AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, (primaryRadius < 1) ? 1 : primaryRadius);
		AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, 1);
		AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, secondaryRadius);
	}

	/**
	 * @return The source's volume.
	 */
	protected float getVolume() {
		return volume;
	}

	/**
	 * Sets the volume of the source. Any sounds played on this source will be played at this volume.
	 *
	 * @param newVolume The new volume.
	 */
	protected void setVolume(final float newVolume) {
		if (newVolume != volume) {
			AL10.alSourcef(sourceID, AL10.AL_GAIN, newVolume);
			volume = newVolume;
		}
	}

	/**
	 * @param position The 3D position of the source in the world.
	 */
	protected void setPosition(final Vector3f position) {
		AL10.alSource3f(sourceID, AL10.AL_POSITION, position.x, position.y, position.z);
	}

	/**
	 * @param loop Whether the source should play sounds on loop or not.
	 */
	protected void loop(final boolean loop) {
		AL10.alSourcei(sourceID, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	/**
	 * Plays a sound and returns an {@link AudioController} which allows the settings of the source to be changed while the sound is playing.
	 * Streaming is automatically handled if the sound file is large.
	 *
	 * @param sound The sound to be played.
	 *
	 * @return The controller for the playing of this sound.
	 */
	protected AudioController playSound(final Sound sound) {
		if (!sound.isLoaded()) {
			return null;
		}

		stop();
		active = true;
		currentController = new AudioController(this);

		if (sound.needsStreaming()) {
			queue(sound.getBufferID());
			AL10.alSourcei(sourceID, AL10.AL_LOOPING, AL10.AL_FALSE);
			StreamManager.STREAMER.stream(sound, this, currentController);
		} else {
			AL10.alSourcei(sourceID, AL10.AL_LOOPING, AL10.AL_FALSE);
			AL10.alSourcei(sourceID, AL10.AL_BUFFER, sound.getBufferID());
		}

		AL10.alSourcePlay(sourceID);
		return currentController;
	}

	/**
	 * Stops the source playing the current sound.
	 */
	protected void stop() {
		if (isPlaying()) {
			AL10.alSourceStop(sourceID);
		}

		setInactive();
	}

	/**
	 * Indicates that the source has finished playing the current sound. This notifies the current controller as well as removing any buffers that were queued to this source.
	 */
	protected void setInactive() {
		if (active) {
			AL10.alSourcei(sourceID, AL10.AL_BUFFER, AL10.AL_NONE);
			currentController.setInactive();

			for (int i = 0; i < getFinishedBuffersCount(); i++) {
				unqueue();
			}

			active = false;
		}
	}

	/**
	 * Removes the top buffer that has already been played from the queue (for use when streaming).
	 */
	protected void unqueue() {
		AL10.alSourceUnqueueBuffers(sourceID);
	}

	/**
	 * @return The number of buffers in the queue that have already been played (for use when streaming).
	 */
	protected final int getFinishedBuffersCount() {
		return AL10.alGetSourcei(sourceID, AL10.AL_BUFFERS_PROCESSED);
	}

	/**
	 * @return {@code true} if the source is currently playing a sound.
	 */
	protected final boolean isPlaying() {
		return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	/**
	 * Queues up a buffer to be played by this source as soon as it has finished playing the current buffer (for use when streaming).
	 *
	 * @param buffer The buffer to be queued.
	 */
	protected void queue(final int buffer) {
		AL10.alSourceQueueBuffers(sourceID, buffer);
	}

	/**
	 * Deletes the source.
	 */
	protected void delete() {
		AL10.alDeleteSources(sourceID);
	}
}
