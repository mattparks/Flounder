package flounder.steam;

import flounder.framework.*;

public class FlounderSteam extends Module {
	private static final FlounderSteam INSTANCE = new FlounderSteam();
	public static final String PROFILE_TAB_NAME = "Steam";

	public FlounderSteam() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME);
	}

	@Override
	public void init() {
		/*try {
			if (!SteamAPI.init()) {
				// Steamworks initialization error, e.g. Steam client not running
			}
		} catch (SteamException e) {
			FlounderLogger.exception(e);
		}

		SteamAPI.printDebugInfo(System.out);*/
	}

	@Override
	public void update() {
	/*	if (SteamAPI.isSteamRunning()) {
			SteamAPI.runCallbacks();
		}*/
	}

	@Override
	public void profile() {
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		//	SteamAPI.shutdown();
	}
}
