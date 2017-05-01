package flounder.lwjgl3.sounds;

import flounder.devices.*;
import flounder.maths.vectors.*;
import flounder.sounds.*;

import static org.lwjgl.openal.AL10.*;

public class LwjglSoundSource extends SoundSource {
	private int sourceID;

	public LwjglSoundSource() {
		super();
		this.sourceID = createSource();

		alSource3f(sourceID, AL_POSITION, 0.0f, 0.0f, 0.0f);
		alSource3f(sourceID, AL_VELOCITY, 1.0f, 0.0f, 0.0f);
		alSourcef(sourceID, AL_ROLLOFF_FACTOR, 0.0f);
		alSourcef(sourceID, AL_GAIN, volume);
		alSourcef(sourceID, AL_PITCH, pitch);
	}

	/**
	 * Gets the ID of the newly created OpenAL source.
	 *
	 * @return The source ID.
	 */
	private static int createSource() {
		int sourceID = alGenSources();

		if (alGetError() != AL_NO_ERROR) {
			System.err.println("Problem creating sound source!");
		}

		return sourceID;
	}

	@Override
	protected void setRange(float radius) {
		alSourcef(sourceID, AL_REFERENCE_DISTANCE, 1.0f);
		alSourcef(sourceID, AL_ROLLOFF_FACTOR, 1.0f);
		alSourcef(sourceID, AL_MAX_DISTANCE, radius);
	}

	@Override
	protected void setUndiminishing() {
		alSourcef(sourceID, AL_ROLLOFF_FACTOR, 0);
	}

	@Override
	protected void setRanges(float primaryRadius, float secondaryRadius) {
		alSourcef(sourceID, AL_REFERENCE_DISTANCE, (primaryRadius < 1.0f) ? 1.0f : primaryRadius);
		alSourcef(sourceID, AL_ROLLOFF_FACTOR, 1.0f);
		alSourcef(sourceID, AL_MAX_DISTANCE, secondaryRadius);
	}

	@Override
	protected float getVolume() {
		return alGetSourcef(sourceID, AL_GAIN);
	}

	@Override
	protected void setVolume(float newVolume) {
		if (newVolume != volume) {
			alSourcef(sourceID, AL_GAIN, newVolume);
			volume = newVolume;
		}
	}

	@Override
	protected float getPitch() {
		return alGetSourcef(sourceID, AL_PITCH);
	}

	@Override
	protected void setPitch(float newPitch) {
		if (newPitch != pitch) {
			alSourcef(sourceID, AL_PITCH, newPitch);
			pitch = newPitch;
		}
	}

	@Override
	protected void setPosition(Vector3f position) {
		alSource3f(sourceID, AL_POSITION, position.x, position.y, position.z);
	}

	@Override
	protected void loop(boolean loop) {
		alSourcei(sourceID, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
	}

	@Override
	protected AudioController playSound(Sound sound) {
		if (!sound.isLoaded()) {
			return null;
		}

		stop();
		active = true;
		currentController = new AudioController(this);

		if (sound.needsStreaming()) {
			queue(sound.getBufferID());
			alSourcei(sourceID, AL_LOOPING, AL_FALSE);
			FlounderSound.getStreamManager().stream(sound, this, currentController);
		} else {
			alSourcei(sourceID, AL_LOOPING, AL_FALSE);
			alSourcei(sourceID, AL_BUFFER, sound.getBufferID());
		}

		alSourcePlay(sourceID);
		return currentController;
	}

	@Override
	protected void stop() {
		if (isPlaying()) {
			alSourceStop(sourceID);
		}

		setInactive();
	}

	@Override
	protected void setInactive() {
		if (active) {
			alSourcei(sourceID, AL_BUFFER, AL_NONE);

			if (currentController != null) {
				currentController.setInactive();
			}

			for (int i = 0; i < getFinishedBuffersCount(); i++) {
				unqueue();
			}

			active = false;
		}
	}

	@Override
	protected void unqueue() {
		alSourceUnqueueBuffers(sourceID);
	}

	@Override
	protected int getFinishedBuffersCount() {
		return alGetSourcei(sourceID, AL_BUFFERS_PROCESSED);
	}

	@Override
	protected boolean isPlaying() {
		return alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING;
	}

	@Override
	protected void queue(int buffer) {
		alSourceQueueBuffers(sourceID, buffer);
	}

	@Override
	protected void pause() {
		if (active) {
			alSourcePause(sourceID);
			active = false;
		}
	}

	@Override
	protected void unpause() {
		if (!active) {
			alSourcePlay(sourceID);
			active = true;
		}
	}

	@Override
	protected void delete() {
		alDeleteSources(sourceID);
	}
}
