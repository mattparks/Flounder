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
	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");

	private Vector3f m_cameraPosition;

	private SourcePoolManager m_sourcePool;
	private MusicPlayer m_musicPlayer;
	private ALContext m_context;

	/**
	 * Initializes all the sound related things. Should be called when the game loads.
	 */
	protected DeviceSound() {
		m_cameraPosition = new Vector3f(0.0f, 0.0f, 0.0f);
		m_context = ALContext.create();
		int alError = AL10.alGetError();

		if (alError != GL_NO_ERROR) {
			System.out.println("OpenAL Error: " + alError);
		}

		AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
		StreamManager.STREAMER.start();
		m_sourcePool = new SourcePoolManager();
		m_musicPlayer = new MusicPlayer();
		m_musicPlayer.setVolume(OptionsAudio.SOUND_VOLUME);
	}

	/**
	 * Updates the listener's position, the music player and the source pool manager.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(final float delta) {
		m_cameraPosition.set(FlounderEngine.getCamera().getPosition());
		AL10.alListener3f(AL10.AL_POSITION, m_cameraPosition.x, m_cameraPosition.y, m_cameraPosition.z);
		m_musicPlayer.update(delta);
		m_sourcePool.update();
	}

	/**
	 * @return The cameras position.
	 */
	public Vector3f getCameraPosition() {
		return m_cameraPosition;
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

		return m_sourcePool.play(playRequest);
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

		return m_sourcePool.play(PlayRequest.newSystemPlayRequest(sound));
	}

	/**
	 * @return The background music player.
	 */
	public MusicPlayer getMusicPlayer() {
		return m_musicPlayer;
	}

	/**
	 * Closes the OpenAL audio system, do not use sounds after calling this.
	 */
	protected void dispose() {
		StreamManager.STREAMER.kill();
		m_sourcePool.cleanUp();
		m_musicPlayer.cleanUp();
		SoundLoader.cleanUp();
		m_context.destroy();
	}
}
