package flounder.steam;

import flounder.framework.*;

public class FlounderSteam extends Module {
	private static final FlounderSteam INSTANCE = new FlounderSteam();
	public static final String PROFILE_TAB_NAME = "Steam";

	public FlounderSteam() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		/*try {
			if (!SteamAPI.init()) {
				// Steamworks initialization error, e.g. Steam client not running
			}
		} catch (SteamException e) {
			FlounderLogger.get().exception(e);
		}

		SteamAPI.printDebugInfo(System.out);*/
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	/*	if (SteamAPI.isSteamRunning()) {
			SteamAPI.runCallbacks();
		}*/
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		//	SteamAPI.shutdown();
	}
}
