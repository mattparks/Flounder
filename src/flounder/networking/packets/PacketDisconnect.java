package flounder.networking.packets;

import flounder.engine.*;
import flounder.networking.*;

import java.net.*;

/**
 * A packet that is used when a client / server disconnects.
 */
public class PacketDisconnect extends Packet {
	private String username;

	/**
	 * Creates a new disconnect packet.
	 *
	 * @param data The data to create from.
	 */
	public PacketDisconnect(byte[] data) {
		this.username = readData(data);
	}

	/**
	 * Creates a new disconnect packet.
	 *
	 * @param username The username that is disconnecting.
	 */
	public PacketDisconnect(String username) {
		this.username = username;
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public void clientHandlePacket(Client client, InetAddress address, int port) {
		FlounderEngine.getLogger().log("[" + address.getHostAddress() + ":" + port + "] " + username + " has quit the game.");
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		System.out.println("[" + address.getHostAddress() + ":" + port + "] " + username + " has disconnected.");
		server.removeConnection(this);
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username).getBytes();
	}

	/**
	 * Gets the username of the client that disconnected.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}
}
