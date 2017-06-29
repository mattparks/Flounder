package flounder.guis;

import flounder.maths.*;

public class InputDelay {
	private Timer delayTimer;
	private Timer repeatTimer;
	private boolean delayOver;

	public InputDelay() {
		this.delayTimer = new Timer(0.4);
		this.repeatTimer = new Timer(0.1);
		this.delayOver = false;
	}

	public void update(boolean keyIsDown) {
		if (keyIsDown) {
			delayOver = delayTimer.isPassedTime();
		} else {
			delayOver = false;
			delayTimer.resetStartTime();
			repeatTimer.resetStartTime();
		}
	}

	public boolean canInput() {
		if (delayOver && repeatTimer.isPassedTime()) {
			repeatTimer.resetStartTime();
			return true;
		}

		return false;
	}
}
