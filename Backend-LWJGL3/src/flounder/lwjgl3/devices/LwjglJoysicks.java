package flounder.lwjgl3.devices;

import flounder.devices.*;
import flounder.framework.*;

public class LwjglJoysicks extends FlounderJoysticks {
	public LwjglJoysicks() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		super.init();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		super.update();
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		super.profile();
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();
	}
}
