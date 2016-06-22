package flounder.networking.client;

import flounder.engine.*;
import flounder.networking.packets.*;

import java.io.*;
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
		} catch (SocketException e) {
			FlounderEngine.getLogger().exception(e);
		} catch (UnknownHostException e) {
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
				FlounderEngine.getLogger().error("Client socket could not receive data!");
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
				handleLogin((Packet00Login) packet, address, port);
				break;
			case DISCONNECT:
				packet = new Packet01Disconnect(data);
				handleDisconnect((Packet01Disconnect) packet, address, port);
				break;
		}
	}

	private void handleLogin(Packet00Login packet, InetAddress address, int port) {
		FlounderEngine.getLogger().log("[" + address.getHostAddress() + ":" + port + "] " + (packet).getUsername() + " has joined the game.");
	}

	private void handleDisconnect(Packet01Disconnect packet, InetAddress address, int port) {
		FlounderEngine.getLogger().log("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername() + " has quit the game.");
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
