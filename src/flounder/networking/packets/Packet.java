package flounder.networking.packets;

import flounder.networking.client.*;
import flounder.networking.server.*;

public abstract class Packet {
	public byte packetID;

	public Packet(int packetID) {
		this.packetID = (byte) packetID;
	}

	public static PacketType lookupPacket(String packetID) {
		try {
			return lookupPacket(Integer.parseInt(packetID));
		} catch (NumberFormatException e) {
			return PacketType.INVALID;
		}
	}

	public static PacketType lookupPacket(int id) {
		for (PacketType p : PacketType.values()) {
			if (p.getID() == id) {
				return p;
			}
		}

		return PacketType.INVALID;
	}

	public abstract void writeData(Client client);

	public abstract void writeData(Server server);

	public String readData(byte[] data) {
		return new String(data).trim().substring(2);
	}

	public abstract byte[] getData();

	public enum PacketType {
		INVALID(-1), LOGIN(00), DISCONNECT(01);

		private int packetID;

		PacketType(int packetID) {
			this.packetID = packetID;
		}

		public int getID() {
			return packetID;
		}
	}
}
