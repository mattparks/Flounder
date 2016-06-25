package flounder.networking;

import flounder.engine.*;
import flounder.networking.packets.*;

/**
 * A manager that manages the current network connections of the engine.
 */
public class FlounderNetwork implements IModule {
	private Server socketServer;
	private Client socketClient;
	private String username;
	private int port;

	/**
	 * Creates a new network manager.
	 *
	 * @param port The port for the client and server to run off of.
	 */
	public FlounderNetwork(int port) {
		this.port = port;
	}

	@Override
	public void init() {
		username = "USERNAME" + ((int) (Math.random() * 10000));
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
	}

	/**
	 * Starts the server.
	 */
	public void startServer() {
		socketServer = new Server(port);
		socketServer.start();
	}

	/**
	 * Starts the client.
	 */
	public void startClient() {
		socketClient = new Client("localhost", port);
		socketClient.start();

		PacketLogin loginPacket = new PacketLogin(username);
		loginPacket.writeData(socketClient);
	}

	/**
	 * Closes the server.
	 */
	public void closeServer() {
		if (socketServer != null) {
			new PacketDisconnect("server").writeData(socketServer);
			socketServer.dispose();
			socketServer = null;
		}
	}

	/**
	 * Closes the client.
	 */
	public void closeClient() {
		if (socketClient != null) {
			new PacketDisconnect(username).writeData(socketClient);
			socketClient.dispose();
			socketClient = null;
		}
	}

	/**
	 * Gets the server currently running from this engine.
	 *
	 * @return The server running from this server.
	 */
	public Server getSocketServer() {
		return socketServer;
	}

	/**
	 * Gets the client currently running.
	 *
	 * @return The client running.
	 */
	public Client getSocketClient() {
		return socketClient;
	}

	@Override
	public void dispose() {
		closeServer();
		closeClient();
	}
}
