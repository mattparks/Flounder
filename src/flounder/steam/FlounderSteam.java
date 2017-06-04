package flounder.steam;

import flounder.framework.*;

public class FlounderSteam extends Module {
	public FlounderSteam() {
		super();
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

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		//	SteamAPI.shutdown();
	}

	@Module.Instance
	public static FlounderSteam get() {
		return (FlounderSteam) Framework.get().getInstance(FlounderSteam.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Steam";
	}
}
