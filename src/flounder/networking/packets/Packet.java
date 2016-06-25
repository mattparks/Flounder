package flounder.networking.packets;

import flounder.networking.*;

import java.net.*;

/**
 * A class that can be extended to create packets that are sent between servers and clients.
 */
public abstract class Packet {
	/**
	 * Writes the data from the client to the server.
	 *
	 * @param client The client to send the data from.
	 */
	public abstract void writeData(Client client);

	/**
	 * Writes the data from the server to all connected clients.
	 *
	 * @param server The server to send the data from.
	 */
	public abstract void writeData(Server server);

	/**
	 * Reads bytes of data to get the packets contents.
	 *
	 * @param data The data to read.
	 *
	 * @return The message read from the data.
	 */
	public String readData(byte[] data) {
		String dataString = new String(data).trim();
		return dataString.substring(1, dataString.length()).split("]:")[1];
	}

	/**
	 * This is run when the client receives the packet.
	 *
	 * @param client The client that is processing the packet.
	 * @param address The address where the data came from.
	 * @param port The  port the data came from.
	 */
	public abstract void clientHandlePacket(Client client, InetAddress address, int port);

	/**
	 * This is run when the server receives the packet.
	 *
	 * @param server The server that is processing the packet.
	 * @param address The address where the data came from.
	 * @param port The  port the data came from.
	 */
	public abstract void serverHandlePacket(Server server, InetAddress address, int port);

	/**
	 * Gets the data contained in the packet.
	 *
	 * @return The data contained.
	 */
	public abstract byte[] getData();

	/**
	 * Gets the packet class prefix, should be appended to the packets message before {@link #getData()}.
	 *
	 * @return The packets class prefix.
	 */
	public String getDataPrefix() {
		return "[" + this.getClass().getName() + "]:";
	}
}
