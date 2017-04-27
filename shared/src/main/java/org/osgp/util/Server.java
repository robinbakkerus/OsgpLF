package org.osgp.util;

public class Server {

	private String host;
	private int port;
	private String rpc;
	
	protected Server(String host, int port, final String rpc) {
		super();
		this.host = host;
		this.port = port;
		this.rpc = rpc;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRpc() {
		return rpc;
	}

	@Override
	public String toString() {
		return "Server [host=" + host + ", port=" + port + ", rpc=" + rpc + "]";
	}

}
