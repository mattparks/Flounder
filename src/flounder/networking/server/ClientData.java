package flounder.networking.server;

import java.net.*;

public class ClientData {
	public InetAddress ipAddress;
	public int port;
	public String username;

	public ClientData(String username, InetAddress ipAddress, int port) {
		this.username = username;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getUsername() {
		return username;
	}
}
