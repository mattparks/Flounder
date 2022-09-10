package com.flounder.collada.animation;

public class AnimationData {
	private final float lengthSeconds;
	private final KeyFrameData[] keyFrames;

	public AnimationData(float lengthSeconds, KeyFrameData[] keyFrames) {
		this.lengthSeconds = lengthSeconds;
		this.keyFrames = keyFrames;
	}

	public float getLengthSeconds() {
		return lengthSeconds;
	}

	public KeyFrameData[] getKeyFrames() {
		return keyFrames;
	}
}
