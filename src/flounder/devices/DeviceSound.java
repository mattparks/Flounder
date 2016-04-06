package flounder.devices;

import flounder.engine.*;
import flounder.engine.options.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.sounds.*;
import org.lwjgl.openal.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * An Sound Device implemented using OpenAL.
 */
public class DeviceSound {
	// TODO: Live music player volume change.

	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");

	private final Vector3f cameraPosition;
	private final SourcePoolManager sourcePool;
	private final MusicPlayer musicPlayer;
	private final ALContext context;

	/**
	 * Initializes all the sound related things. Should be called when the game loads.
	 */
	protected DeviceSound() {
		cameraPosition = new Vector3f(0.0f, 0.0f, 0.0f);
		context = ALContext.create();
		final int alError = AL10.alGetError();

		if (alError != GL_NO_ERROR) {
			FlounderLogger.error("OpenAL Error: " + alError);
		}

		AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
		StreamManager.STREAMER.start();
		sourcePool = new SourcePoolManager();
		musicPlayer = new MusicPlayer();
		musicPlayer.setVolume(OptionsAudio.SOUND_VOLUME);
	}

	/**
	 * Updates the listener's position, the music player and the source pool manager.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(final float delta) {
		cameraPosition.set(FlounderEngine.getCamera().getPosition());
		AL10.alListener3f(AL10.AL_POSITION, cameraPosition.x, cameraPosition.y, cameraPosition.z);
		musicPlayer.update(delta);
		sourcePool.update();
	}

	/**
	 * @return The cameras position.
	 */
	public Vector3f getCameraPosition() {
		return cameraPosition;
	}

	/**
	 * Plays a sound that should be emitted from somewhere in the 3D world.
	 *
	 * @param playRequest The request containing the sound and all the settings for the playing of the sound.
	 *
	 * @return The controller for the source which plays the sound. Returns {@code null} if no source was available to play the sound.
	 */
	public AudioController play3DSound(final PlayRequest playRequest) {
		if (!playRequest.getSound().isLoaded()) {
			return null;
		}

		return sourcePool.play(playRequest);
	}

	/**
	 * Send a request to play a system sound effect at full volume. The request is sent to the {@link SourcePoolManager} which will find a source to play the sound.
	 *
	 * @param sound The sound to be played.
	 *
	 * @return The controller for the playing of this sound.
	 */
	public AudioController playSystemSound(final Sound sound) {
		if (!sound.isLoaded()) {
			return null;
		}

		return sourcePool.play(PlayRequest.newSystemPlayRequest(sound));
	}

	/**
	 * @return The background music player.
	 */
	public MusicPlayer getMusicPlayer() {
		return musicPlayer;
	}

	/**
	 * Closes the OpenAL audio system, do not use sounds after calling this.
	 */
	protected void dispose() {
		StreamManager.STREAMER.kill();
		sourcePool.cleanUp();
		musicPlayer.cleanUp();
		SoundLoader.cleanUp();
		context.destroy();
	}
}
