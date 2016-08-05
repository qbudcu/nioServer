package org.lpf;

import java.io.IOException;

import org.lpf.core.NioServer;

public class NioServerTest {

	public static void main(String[] args) throws IOException {
		NioServer server = new NioServer(6018);
		server.listen();
	}

}
