package flounder.networking;

import flounder.framework.*;
import flounder.logger.*;
import flounder.profiling.*;

/**
 * A module used for handling networking, servers, clients, and packets.
 */
public class FlounderNetwork extends Module {
	private static final FlounderNetwork INSTANCE = new FlounderNetwork();
	public static final String PROFILE_TAB_NAME = "Network";

	public static final int DEFAULT_PORT = 2266;

	private Server socketServer;
	private Client socketClient;
	private String username;
	private int port;
	private boolean setup;

	/**
	 * Creates a new network manager.
	 */
	public FlounderNetwork() {
		super(ModuleUpdate.UPDATE_POST, PROFILE_TAB_NAME, FlounderLogger.class);
	}

	@Override
	public void init() {
		if (!setup) {
			this.port = DEFAULT_PORT;
			this.setup = true;
		}

		//	this.username = "USERNAME" + ((int) (Math.random() * 10000));
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Username", username);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Port", port);
	}

	/**
	 * Starts the server.
	 */
	public static void startServer(int port) {
		INSTANCE.username = "server";

		FlounderLogger.log("Starting server!");
		INSTANCE.socketServer = new Server(port);
		INSTANCE.socketServer.start();
	}

	/**
	 * Starts the client.
	 */
	public static void startClient(String username, String ipAddress, int port) {
		INSTANCE.username = username;

		FlounderLogger.log("Starting Client on server " + ipAddress);
		INSTANCE.socketClient = new Client(ipAddress, port); // Default ip: "localhost"
		INSTANCE.socketClient.start();
	}

	/**
	 * Closes the server.
	 */
	public static void closeServer() {
		if (INSTANCE.socketServer != null) {
			FlounderLogger.log("Closing server!");
			INSTANCE.socketServer.dispose();
			INSTANCE.socketServer = null;
		}
	}

	/**
	 * Closes the client.
	 */
	public static void closeClient() {
		if (INSTANCE.socketClient != null) {
			FlounderLogger.log("Closing client!");
			INSTANCE.socketClient.dispose();
			INSTANCE.socketClient = null;
		}
	}

	/**
	 * Gets the server currently running from this engine.
	 *
	 * @return The server running from this server.
	 */
	public static Server getSocketServer() {
		return INSTANCE.socketServer;
	}

	/**
	 * Gets the client currently running.
	 *
	 * @return The client running.
	 */
	public static Client getSocketClient() {
		return INSTANCE.socketClient;
	}

	public static String getUsername() {
		return INSTANCE.username;
	}

	public static int getPort() {
		return INSTANCE.port;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		closeServer();
		closeClient();
	}
}
