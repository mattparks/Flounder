package flounder.networking;

import flounder.framework.*;
import flounder.logger.*;
import flounder.networking.packets.*;
import flounder.profiling.*;

/**
 * A module used for handling networking, servers, clients, and packets.
 */
public class FlounderNetwork extends Module {
	private static final FlounderNetwork INSTANCE = new FlounderNetwork();
	public static final String PROFILE_TAB_NAME = "Network";

	private static final int DEFAULT_PORT = 2266;

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

	/**
	 * A function called before initialization to configure the network.
	 *
	 * @param port The networks port.
	 */
	public static void setup(int port) {
		if (!INSTANCE.setup) {
			INSTANCE.port = port;
			INSTANCE.setup = true;
		}
	}

	@Override
	public void init() {
		if (!setup) {
			this.port = DEFAULT_PORT;
			this.setup = true;
		}

		this.username = "USERNAME" + ((int) (Math.random() * 10000));
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
	public static void startServer() {
		FlounderLogger.log("Starting server!");
		INSTANCE.socketServer = new Server(INSTANCE.port);
		INSTANCE.socketServer.start();
	}

	/**
	 * Starts the client.
	 */
	public static void startClient() {
		FlounderLogger.log("Starting Client!");
		INSTANCE.socketClient = new Client("localhost", INSTANCE.port);
		INSTANCE.socketClient.start();

		PacketLogin loginPacket = new PacketLogin(INSTANCE.username);
		loginPacket.writeData(INSTANCE.socketClient);
	}

	/**
	 * Closes the server.
	 */
	public static void closeServer() {
		if (INSTANCE.socketServer != null) {
			FlounderLogger.log("Closing server!");

			new PacketDisconnect("server").writeData(INSTANCE.socketServer);
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

			new PacketDisconnect(INSTANCE.username).writeData(INSTANCE.socketClient);
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
