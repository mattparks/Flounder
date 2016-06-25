package flounder.networking;

import flounder.engine.*;
import flounder.networking.packets.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

/**
 * A server that can send data to clients and relieve data.
 */
public class Server extends Thread {
	private DatagramSocket socket;
	private List<ClientInfo> connected;

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

		if (!message.contains("]:")) {
			return;
		}

		String className = message.substring(1, message.length()).split("]:")[0];
		Packet packet = null;

		try {
			Class<?> clazz = Class.forName(className);
			Constructor<?> ctor = clazz.getConstructor(byte[].class);
			Object object = ctor.newInstance(new Object[]{data});
			packet = (Packet) object;
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			FlounderEngine.getLogger().error("Server could not load packet with the class of " + className);
			e.printStackTrace();
		}

		if (packet != null) {
			packet.serverHandlePacket(this, address, port);
		}
	}

	/**
	 * Adds the connection of a client.
	 *
	 * @param player The client data to add.
	 * @param packet The connect packet.
	 */
	public void addConnection(ClientInfo player, PacketLogin packet) {
		boolean alreadyConnected = false;

		for (ClientInfo p : connected) {
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}

				if (p.port == -1) {
					p.port = player.port;
				}

				alreadyConnected = true;
			} else {
				// Relay to the current connected player that there is a new player
				sendData(packet.getData(), p.ipAddress, p.port);

				// Relay to the new player that the currently connect player exists
				packet = new PacketLogin(p.getUsername());
				sendData(packet.getData(), player.ipAddress, player.port);
			}
		}

		if (!alreadyConnected) {
			this.connected.add(player);
		}
	}

	/**
	 * Removes the connection of a client.
	 *
	 * @param packet The disconnect packet.
	 */
	public void removeConnection(PacketDisconnect packet) {
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
	public ClientInfo getPlayerMP(String username) {
		for (ClientInfo player : connected) {
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

		for (ClientInfo player : connected) {
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
		for (ClientInfo p : connected) {
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
