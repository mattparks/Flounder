package flounder.networking;

import java.net.*;

/**
 * Data about the connected client, used primarily in the server.
 */
public class ClientInfo {
	public InetAddress ipAddress;
	public int port;
	public String username;

	/**
	 * Creates a new client data.
	 *
	 * @param username The clients username.
	 * @param ipAddress The clients IP address.
	 * @param port The port the client is connected to.
	 */
	public ClientInfo(String username, InetAddress ipAddress, int port) {
		this.username = username;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	/**
	 * Gets the clients username.
	 *
	 * @return The clients username.
	 */
	public String getUsername() {
		return username;
	}
}
