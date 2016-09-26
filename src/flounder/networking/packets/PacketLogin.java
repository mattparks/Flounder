package flounder.networking.packets;

import flounder.logger.*;
import flounder.networking.*;

import java.net.*;

/**
 * A packet that is used when a client connects.
 */
public class PacketLogin extends Packet {
	private String username;

	/**
	 * Creates a new connect packet.
	 *
	 * @param data The data to create from.
	 */
	public PacketLogin(byte[] data) {
		this.username = readData(data);
	}

	/**
	 * Creates a new connect packet.
	 *
	 * @param username The username that is connecting.
	 */
	public PacketLogin(String username) {
		this.username = username;
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sentDataToOtherClient(getData(), username);
	}

	@Override
	public void clientHandlePacket(Client client, InetAddress address, int port) {
		FlounderLogger.log("[" + address.getHostAddress() + ":" + port + "] " + username + " has joined the game.");
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		// if (address.getHostAddress().equalsIgnoreCase("127.0.0.1")) // Server = Client
		System.out.println("[" + address.getHostAddress() + ":" + port + "] " + username + " has connected.");
		ClientInfo player = new ClientInfo(username, address, port);
		server.addConnection(player, this);
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username).getBytes();
	}

	/**
	 * Gets the username of the client that connected.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}
}
