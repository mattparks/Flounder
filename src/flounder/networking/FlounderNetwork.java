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

	/**
	 * Creates a new network manager.
	 */
	public FlounderNetwork() {
		super(ModuleUpdate.UPDATE_POST, PROFILE_TAB_NAME, FlounderLogger.class);
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Username", username);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Port", getPort());
	}

	/**
	 * Starts the server.
	 *
	 * @param port The port to start the server on.
	 */
	public static void startServer(int port) {
		INSTANCE.username = "server";

		FlounderLogger.log("Starting server on port " + port);
		INSTANCE.socketServer = new Server(port);
		INSTANCE.socketServer.start();
	}

	/**
	 * Starts the client.
	 *
	 * @param username The username for the client to use.
	 * @param ipAddress The ip address to connect the client on.
	 * @param port The port to connect the client on.
	 */
	public static void startClient(String username, String ipAddress, int port) {
		INSTANCE.username = username;

		FlounderLogger.log("Starting Client on server " + ipAddress + ":" + port);
		INSTANCE.socketClient = new Client(ipAddress, port);
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
		if (INSTANCE.socketClient != null) {
			return INSTANCE.socketClient.getServerPort();
		} else if (INSTANCE.socketServer != null) {
			return INSTANCE.socketServer.getServerPort();
		}

		return DEFAULT_PORT;
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
