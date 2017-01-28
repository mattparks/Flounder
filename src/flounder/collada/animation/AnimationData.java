package flounder.collada.animation;

public class AnimationData {
	private final float lengthSeconds;
	private final AnimationKeyFrameData[] keyFrames;

	public AnimationData(float lengthSeconds, AnimationKeyFrameData[] keyFrames) {
		this.lengthSeconds = lengthSeconds;
		this.keyFrames = keyFrames;
	}

	public float getLengthSeconds() {
		return lengthSeconds;
	}

	public AnimationKeyFrameData[] getKeyFrames() {
		return keyFrames;
	}
}
