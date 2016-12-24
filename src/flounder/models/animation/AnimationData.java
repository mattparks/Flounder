package flounder.models.animation;

import flounder.resources.*;

public class AnimationData {
	public MyFile file;

	public AnimationData(MyFile file) {
		this.file = file;
	}

	public void createRaw(Animation animation) {
		animation.loadData();
	}

	public void destroy() {
	}
}
