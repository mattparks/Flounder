package flounder.networking;

import flounder.framework.*;
import flounder.logger.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

/**
 * A client that is with a connection on the server, that can send and recede packets.
 */
public class Client extends Thread {
	private InetAddress ipAddress;
	private DatagramSocket socket;
	private int serverPort;

	/**
	 * Creates a new client.
	 *
	 * @param ipAddress The IP address to connect to.
	 * @param port The IP's serverPort to connect with.
	 */
	public Client(String ipAddress, int port) {
		try {
			super.setName("client");
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
			this.serverPort = port;
		} catch (SocketException | UnknownHostException e) {
			FlounderLogger.exception(e);
		}
	}

	@Override
	public void run() {
		while (Framework.isRunning()) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);

			try {
				socket.receive(packet);
			} catch (IOException e) {
				FlounderLogger.error("Client socket could not receive data!");
				FlounderLogger.exception(e);
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
			FlounderLogger.error("Client could not load packet with the class of " + className);
			e.printStackTrace();
		}

		if (packet != null) {
			packet.clientHandlePacket(this, address, port);
		}
	}

	/**
	 * Sends bytes of data back to the server.
	 *
	 * @param data The data to send.
	 */
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, serverPort);

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getIpAddress() {
		return ipAddress.getHostAddress();
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
