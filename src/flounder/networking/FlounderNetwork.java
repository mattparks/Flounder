package flounder.networking;

import flounder.framework.*;
import flounder.logger.*;

/**
 * A module used for handling networking, servers, clients, and packets.
 */
public class FlounderNetwork extends Module {
	public static final int DEFAULT_PORT = 2266;

	private Server socketServer;
	private Client socketClient;
	private String username;

	/**
	 * Creates a new network manager.
	 */
	public FlounderNetwork() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_POST)
	public void update() {
	}

	/**
	 * Starts the server.
	 *
	 * @param port The port to start the server on.
	 */
	public void startServer(int port) {
		this.username = "server";

		FlounderLogger.get().log("Starting server on port " + port);
		this.socketServer = new Server(port);
		this.socketServer.start();
	}

	/**
	 * Starts the client.
	 *
	 * @param username The username for the client to use.
	 * @param ipAddress The ip address to connect the client on.
	 * @param port The port to connect the client on.
	 */
	public void startClient(String username, String ipAddress, int port) {
		this.username = username;

		FlounderLogger.get().log("Starting Client on server " + ipAddress + ":" + port);
		this.socketClient = new Client(ipAddress, port);
		this.socketClient.start();
	}

	/**
	 * Closes the server.
	 */
	public void closeServer() {
		if (this.socketServer != null) {
			FlounderLogger.get().log("Closing server!");
			this.socketServer.dispose();
			this.socketServer = null;
		}
	}

	/**
	 * Closes the client.
	 */
	public void closeClient() {
		if (this.socketClient != null) {
			FlounderLogger.get().log("Closing client!");
			this.socketClient.dispose();
			this.socketClient = null;
		}
	}

	/**
	 * Gets the server currently running from this engine.
	 *
	 * @return The server running from this server.
	 */
	public Server getSocketServer() {
		return this.socketServer;
	}

	/**
	 * Gets the client currently running.
	 *
	 * @return The client running.
	 */
	public Client getSocketClient() {
		return this.socketClient;
	}

	public String getUsername() {
		return this.username;
	}

	public int getPort() {
		if (this.socketClient != null) {
			return this.socketClient.getServerPort();
		} else if (this.socketServer != null) {
			return this.socketServer.getServerPort();
		}

		return DEFAULT_PORT;
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		closeServer();
		closeClient();
	}

	@Module.Instance
	public static FlounderNetwork get() {
		return (FlounderNetwork) Framework.getInstance(FlounderNetwork.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Network";
	}
}
