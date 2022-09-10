package com.flounder.networking;

import com.flounder.framework.*;
import com.flounder.logger.*;

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
	private int serverPort;

	/**
	 * Creates a new server.
	 *
	 * @param port The port to start on the server.
	 */
	public Server(int port) {
		try {
			super.setName("server");
			this.socket = new DatagramSocket(port);
			this.connected = new ArrayList<>();
			this.serverPort = port;
		} catch (SocketException e) {
			FlounderLogger.get().exception(e);
		}
	}

	@Override
	public void run() {
		while (Framework.get().isRunning()) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);

			try {
				socket.receive(packet);
			} catch (SocketException e) {
				// Ignore.
			} catch (IOException e) {
				FlounderLogger.get().error("Server socket could not receive data!");
				FlounderLogger.get().exception(e);
				System.exit(-1);
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
			FlounderLogger.get().error("Server could not load packet with the class of " + className);
			e.printStackTrace();
		}

		if (packet != null) {
			packet.serverHandlePacket(this, address, port);
		}
	}

	public List<ClientInfo> getConnected() {
		return connected;
	}

	/**
	 * Adds the connection of a client.
	 *
	 * @param player The client data to add.
	 */
	public void addConnection(ClientInfo player) {
		boolean alreadyConnected = false;

		for (ClientInfo p : connected) {
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.getIpAddress() == null) {
					p.setIpAddress(player.getIpAddress());
				}

				if (p.getPort() == -1) {
					p.setPort(player.getPort());
				}

				alreadyConnected = true;
			}
		}

		if (alreadyConnected) {
			removeConnection(player.getUsername());
		}

		this.connected.add(player);
	}

	/**
	 * Removes the connection of a client.
	 *
	 * @param username The disconnecting username.
	 */
	public void removeConnection(String username) {
		this.connected.remove(getPlayerMP(username));
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
	 * Sends bytes of data back to all clients except the one with the username.
	 *
	 * @param data The data to send.
	 * @param excludedUsername The username to exclude.
	 */
	public void sentDataToOtherClient(byte[] data, String excludedUsername) {
		for (ClientInfo p : connected) {
			if (!p.getUsername().equals(excludedUsername)) {
				sendData(data, p.getIpAddress(), p.getPort());
			}
		}
	}

	/**
	 * Sends bytes of data back to all clients.
	 *
	 * @param data The data to send.
	 */
	public void sendDataToAllClients(byte[] data) {
		for (ClientInfo p : connected) {
			sendData(data, p.getIpAddress(), p.getPort());
		}
	}

	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Closes the sockets connection.
	 */
	public void dispose() {
		socket.close();
	}
}
