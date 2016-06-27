package flounder.networking;

import flounder.engine.*;
import flounder.networking.packets.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

/**
 * A client that is with a connection on the server, that can send and recede packets.
 */
public class Client extends Thread {
	private InetAddress ipAddress;
	private int port;
	private DatagramSocket socket;

	/**
	 * Creates a new client.
	 *
	 * @param ipAddress The IP address to connect to.
	 * @param port The IP's port to connect with.
	 */
	public Client(String ipAddress, int port) {
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
			this.port = port;
		} catch (SocketException | UnknownHostException e) {
			FlounderEngine.getLogger().exception(e);
		}
	}

	@Override
	public void run() {
		while (FlounderEngine.isRunning()) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);

			try {
				socket.receive(packet);
			} catch (IOException e) {
				FlounderEngine.getLogger().error("Client socket could not receive data!");
				FlounderEngine.getLogger().exception(e);
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
			FlounderEngine.getLogger().error("Client could not load packet with the class of " + className);
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
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the sockets connection.
	 */
	public void dispose() {
		socket.close();
	}
}
