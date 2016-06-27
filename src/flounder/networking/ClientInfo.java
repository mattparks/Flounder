package flounder.networking;

import java.net.*;

/**
 * Data about the connected client, used primarily in the server.
 */
public class ClientInfo {
	private InetAddress ipAddress;
	private int port;
	private String username;

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
	 * Gets the clients ip address.
	 *
	 * @return The clients ip address.
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/**
	 * Sets the clients ip address.
	 *
	 * @param ipAddress The clients ip address.
	 */
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Gets the clients port.
	 *
	 * @return The clients port.
	 */
	public int getPort() {
		return port;
	}
	/**
	 * Sets the clients port
	 *
	 * @param port The clients port.
	 */
	public void setPort(int port) {
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
