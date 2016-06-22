package flounder.networking.server;

import flounder.engine.*;
import flounder.networking.packets.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A server that can send data to clients and relieve data.
 */
public class Server extends Thread {
	private DatagramSocket socket;
	private List<ClientData> connected;

	/**
	 * Creates a new server.
	 *
	 * @param port The port to start on the server.
	 */
	public Server(int port) {
		try {
			this.socket = new DatagramSocket(port);
			this.connected = new ArrayList<>();
		} catch (SocketException e) {
			FlounderEngine.getLogger().exception(e);
		}
	}

	@Override
	public void run() {
		while (FlounderEngine.isRunning() && socket != null) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);

			try {
				socket.receive(packet);
			} catch (IOException e) {
				FlounderEngine.getLogger().error("Server socket could not receive data!");
				FlounderEngine.getLogger().exception(e);
			}

			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		Packet.PacketType type = Packet.lookupPacket(message.substring(0, 2));

		Packet packet;

		switch (type) {
			default:
			case INVALID:
				packet = null;
				break;
			case LOGIN:
				packet = new Packet00Login(data);
				// if (address.getHostAddress().equalsIgnoreCase("127.0.0.1")) // Server = Client
				System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login) (packet)).getUsername() + " has connected.");
				ClientData player = new ClientData(((Packet00Login) packet).getUsername(), address, port);
				addConnection(player, (Packet00Login) (packet));
				break;
			case DISCONNECT:
				packet = new Packet01Disconnect(data);
				System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect) (packet)).getUsername() + " has disconnected.");
				removeConnection((Packet01Disconnect) (packet));
				break;
		}
	}

	private void addConnection(ClientData player, Packet00Login packet) {
		boolean alredyConnected = false;

		for (ClientData p : connected) {
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}

				if (p.port == -1) {
					p.port = player.port;
				}

				alredyConnected = true;
			} else {
				// Relay to the current connected player that there is a new player
				sendData(packet.getData(), p.ipAddress, p.port);

				// Relay to the new player that the currently connect player exists
				packet = new Packet00Login(p.getUsername());
				sendData(packet.getData(), player.ipAddress, player.port);
			}
		}

		if (!alredyConnected) {
			this.connected.add(player);
		}
	}

	private void removeConnection(Packet01Disconnect packet) {
		this.connected.remove(getPlayerMPIndex(packet.getUsername()));
		packet.writeData(this);
	}

	/**
	 * Gets the client data by username.
	 *
	 * @param username The username to use.
	 *
	 * @return The clients data.
	 */
	public ClientData getPlayerMP(String username) {
		for (ClientData player : connected) {
			if (player.getUsername().equals(username)) {
				return player;
			}
		}

		return null;
	}

	/**
	 * Gets the index the client can be found at, using username as reference.
	 *
	 * @param username The username to use.
	 *
	 * @return The index the client is found at.
	 */
	public int getPlayerMPIndex(String username) {
		int index = 0;

		for (ClientData player : connected) {
			if (player.getUsername().equals(username)) {
				break;
			}

			index++;
		}

		return index;
	}

	/**
	 * Sends byes of data to a ip address on a port.
	 *
	 * @param data The data to send.
	 * @param ipAddress The IP to send to.
	 * @param port The IP's port to receive from.
	 */
	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends bytes of data back to all clients.
	 *
	 * @param data The data to send.
	 */
	public void sendDataToAllClients(byte[] data) {
		for (ClientData p : connected) {
			sendData(data, p.ipAddress, p.port);
		}
	}

	/**
	 * Closes the sockets connection.
	 */
	public void dispose() {
		socket.close();
	}
}
