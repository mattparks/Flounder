package flounder.lwjgl3.devices;

import flounder.camera.*;
import flounder.devices.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.lwjgl3.sounds.*;
import flounder.maths.vectors.*;
import flounder.processing.*;
import flounder.resources.*;
import flounder.sounds.*;
import org.lwjgl.openal.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.openal.AL.createCapabilities;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;

public class LWJGLSound extends IDeviceSound {
	private long device;

	private List<Integer> buffers;

	public LWJGLSound() {
		super();
	}

	@Override
	public void init() {
		// Creates the OpenAL contexts.
		this.device = alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = createCapabilities(device);
		alcMakeContextCurrent(alcCreateContext(device, (IntBuffer) null));
		createCapabilities(deviceCaps);

		this.buffers = new ArrayList<>();

		// Checks for errors.
		int alError = alGetError();

		if (alError != GL_NO_ERROR) {
			FlounderLogger.error("OpenAL Error " + alError);
		}

		// Creates a new sound model.
		alDistanceModel(AL_LINEAR_DISTANCE_CLAMPED);
	}

	@Override
	public void update() {
		Vector3f cameraPosition = FlounderSound.getCameraPosition();
		alListener3f(AL_POSITION, cameraPosition.x, cameraPosition.y, cameraPosition.z);
	}

	@Override
	public SoundSource createPlatformSource() {
		return new LWJGLSoundSource();
	}

	@Override
	public int getOpenAlFormat(int channels, int bitsPerSample) {
		if (channels == 1) {
			return bitsPerSample == 8 ? AL_FORMAT_MONO8 : AL_FORMAT_MONO16;
		} else {
			return bitsPerSample == 8 ? AL_FORMAT_STEREO8 : AL_FORMAT_STEREO16;
		}
	}

	@Override
	public void loadSoundDataIntoBuffer(int bufferID, ByteBuffer data, int format, int sampleRate) {
		alBufferData(bufferID, format, data, sampleRate);
		int error = alGetError();

		if (error != AL_NO_ERROR) {
			FlounderLogger.error("Problem loading sound data into buffer. " + error);
		}
	}

	@Override
	public int generateBuffer() {
		int bufferID = alGenBuffers();
		this.buffers.add(bufferID);
		return bufferID;
	}

	@Override
	public void deleteBuffer(Integer bufferID) {
		this.buffers.remove(bufferID);
		alDeleteBuffers(bufferID);

		if (alGetError() != AL_NO_ERROR) {
			FlounderLogger.warning("Problem deleting sound buffer.");
		}
	}

	@Override
	public void dispose() {
		buffers.forEach(buffer -> {
			if (buffer != null) {
				alDeleteBuffers(buffer);
			}
		});

		if (alGetError() != AL_NO_ERROR) {
			FlounderLogger.warning("Problem deleting sound buffers.");
		}

		alcCloseDevice(device);
	}
}
