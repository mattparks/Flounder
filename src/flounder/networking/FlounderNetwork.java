package flounder.networking;

import flounder.engine.*;
import flounder.logger.*;
import flounder.networking.packets.*;

/**
 * A manager that manages the current network connections of the engine.
 */
public class FlounderNetwork extends IModule {
	private static final FlounderNetwork instance = new FlounderNetwork();

	private static final int PORT = 2266;

	private Server socketServer;
	private Client socketClient;
	private String username;
	private int port;
	private boolean setup;

	/**
	 * Creates a new network manager.
	 */
	public FlounderNetwork() {
		super(ModuleUpdate.AFTER_ENTRANCE, FlounderLogger.class);
	}

	/**
	 * A function called before initialization to configure the network.
	 *
	 * @param port The networks port.
	 */
	public static void setup(int port) {
		if (!instance.setup) {
			instance.port = port;
			instance.setup = true;
		}
	}

	@Override
	public void init() {
		if (!setup) {
			this.port = PORT;
			this.setup = true;
		}

		this.username = "USERNAME" + ((int) (Math.random() * 10000));
	}

	@Override
	public void run() {
	}

	@Override
	public void profile() {
	}

	/**
	 * Starts the server.
	 */
	public static void startServer() {
		FlounderLogger.log("Starting server!");
		instance.socketServer = new Server(instance.port);
		instance.socketServer.start();
	}

	/**
	 * Starts the client.
	 */
	public static void startClient() {
		FlounderLogger.log("Starting Client!");
		instance.socketClient = new Client("localhost", instance.port);
		instance.socketClient.start();

		PacketLogin loginPacket = new PacketLogin(instance.username);
		loginPacket.writeData(instance.socketClient);
	}

	/**
	 * Closes the server.
	 */
	public static void closeServer() {
		if (instance.socketServer != null) {
			FlounderLogger.log("Closing server!");

			new PacketDisconnect("server").writeData(instance.socketServer);
			instance.socketServer.dispose();
			instance.socketServer = null;
		}
	}

	/**
	 * Closes the client.
	 */
	public static void closeClient() {
		if (instance.socketClient != null) {
			FlounderLogger.log("Closing client!");

			new PacketDisconnect(instance.username).writeData(instance.socketClient);
			instance.socketClient.dispose();
			instance.socketClient = null;
		}
	}

	/**
	 * Gets the server currently running from this engine.
	 *
	 * @return The server running from this server.
	 */
	public static Server getSocketServer() {
		return instance.socketServer;
	}

	/**
	 * Gets the client currently running.
	 *
	 * @return The client running.
	 */
	public static Client getSocketClient() {
		return instance.socketClient;
	}

	public static String getUsername() {
		return instance.username;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		closeServer();
		closeClient();
	}
}
