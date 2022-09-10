package com.flounder.sounds;

import com.flounder.devices.*;
import com.flounder.maths.vectors.*;

/**
 * A request that can be sent to the {@link FlounderSound} specifying what sound should be played and what settings the source playing it should use.
 */
public class PlayRequest {
	private Vector3f position;
	private float innerRange;
	private float outerRange;

	private boolean systemSound;
	private boolean loop;

	private Sound sound;
	private float volume;
	private float pitch;

	/**
	 * Creates a new sound play request.
	 *
	 * @param sound The sound that needs to be played.
	 * @param volume The volume that the sound should be played at.
	 * @param pitch The pitch that the sound should be played at.
	 */
	private PlayRequest(Sound sound, float volume, float pitch) {
		this.position = new Vector3f();
		this.innerRange = 1.0f;
		this.outerRange = 1.0f;

		this.systemSound = true;
		this.loop = false;

		this.sound = sound;
		this.volume = volume * MusicPlayer.SOUND_VOLUME;
		this.pitch = pitch;
	}

	/**
	 * Creates a new request for playing a "system sound", which is a sound that isn't part of the 3D world and is simple played at full volume regardless of where the {@link IAudioListener} is.
	 *
	 * @param systemSound The sound to be played.
	 *
	 * @return The newly created play request.
	 */
	public static PlayRequest newSystemPlayRequest(Sound systemSound) {
		return new PlayRequest(systemSound, 1.0f, 1.0f);
	}

	/**
	 * Creates a new request for playing a 3D sound, which is a sound that should appear to be emitted from somewhere in the 3D world.
	 *
	 * @param sound The sound to be played.
	 * @param volume The volume of the sound.
	 * @param pitch The pitch of the sound.
	 * @param position The position from where the sound should be emitted.
	 * @param innerRange The inner range of the sound. Within this range of the sound's position the sound is played at full volume.
	 * @param outerRange The outer range of the sound. Outside this range from the sound's position the sound can't be heard at all.
	 *
	 * @return The newly created request.
	 */
	public static PlayRequest new3dSoundPlayRequest(Sound sound, float volume, float pitch, Vector3f position, float innerRange, float outerRange) {
		PlayRequest request = new PlayRequest(sound, volume, pitch);
		request.systemSound = false;
		request.innerRange = innerRange < 1 ? 1 : innerRange;
		request.outerRange = outerRange;
		request.position.set(position);
		return request;
	}

	/**
	 * @return The position in the 3D world where the sound should be emitted from.
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @return The inner range of the sound. Inside this range the sound is heard at full volume.
	 */
	public float getInnerRange() {
		return innerRange;
	}

	/**
	 * @return The total range of the sound. Outside this range the sound cannot be heard.
	 */
	public float getOuterRange() {
		return outerRange;
	}

	/**
	 * @return {@code true} if the sound is a "system sound", meaning that it should be played at full volume regardless of where the {@link IAudioListener} is.
	 */
	public boolean isSystemSound() {
		return systemSound;
	}

	/**
	 * @return {@code true} if the sound should be played on loop.
	 */
	public boolean isLooping() {
		return loop;
	}

	/**
	 * Indicates that the request is requesting the sound to be played on loop.
	 *
	 * @param loop Whether the sound should be played on loop or not.
	 */
	public void setLooping(boolean loop) {
		this.loop = loop;
	}

	/**
	 * @return The volume at which this sound should be played.
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * @return The pitch at which this sound should be played.
	 */
	public float getPitch() {
		return volume;
	}

	/**
	 * @return The sound that is being requested to be played.
	 */
	public Sound getSound() {
		return sound;
	}
}
