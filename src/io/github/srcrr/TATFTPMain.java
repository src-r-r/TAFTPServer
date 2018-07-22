package io.github.srcrr;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.sun.tools.javac.Main;


public class TATFTPMain {
	
	private final static int BUFFER_SIZE = 1024, DEFAULT_PORT_NUMBER = 69;

	public TATFTPMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		DatagramChannel serverChannel = null;
		TATFTPPacket request = null, response = null;
		ArrayList<TAThread> threads = new ArrayList<TAThread>();
		CloseWatcher watcher = new CloseWatcher(threads);
		
		int portNumber = DEFAULT_PORT_NUMBER;
		boolean doHelp = false;
		for (String a : args) {
			if (a.contains("-h") || a.contains("--h")) {
				doHelp = true;
			} else if (a.matches("[0-9]+")) {
				portNumber = Integer.parseInt(a);
			}
		}
		
		if (doHelp) {
			System.out.println("Usage: ");
			System.out.println("java -jar TATFTP.jar [-h] [port]");
			System.exit(0);
		}
		
		TATFTPPacketFactory factory = TATFTPPacketFactory.getInstance();
		
		try {
			serverChannel = DatagramChannel.open();
			serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DatagramSocket serverSocket = serverChannel.socket();
		
		try {
			serverSocket.bind(new InetSocketAddress(portNumber));
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.err.println("Could not bind to port " + portNumber + ".");
			System.err.println("Try passing in a port number; see `-h` for details.");
			System.exit(1);
		}
		
		if (!serverSocket.isBound()) {
			throw new RuntimeException("Could not bind to the port!");
		}
		
		System.out.println("Totally Awesome TFTP Server");
		System.out.println("Running on " + serverSocket.getLocalAddress().getHostAddress()
							+ ":"
							+ serverSocket.getLocalPort());
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
		
		while (true) {
			System.err.println("waiting for data");
			buf.clear();
			SocketAddress client = null;
			try {
				client = serverChannel.receive(buf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!buf.hasRemaining()) { // i.e. It's empty
				System.err.println("It's empty.");
				response = new TATFTPERROR(TATFTPERROR.ErrorCode.NOT_DEFINED, "Empty data");
				try {
					serverChannel.send(ByteBuffer.wrap(response.toBytes()), serverChannel.getRemoteAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			}
			
			try {
				String s = new String(buf.array(), StandardCharsets.UTF_8);
				System.out.println("Raw Data: '" + buf.array() + "'");
				request = factory.read(buf.array());
			} catch (TAException e1) {
				try {
					e1.asTATFTPERROR().sendTo(serverSocket);
				} catch (NullPointerException e) {
					e.printStackTrace();
					throw new RuntimeException();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (request == null) {
				System.err.println("Request is null.");
				continue;
			}
			
			if (request instanceof TATFTPWRQ) {
				DatagramChannel channel = null;
				try {
					TAThread thread = new TAThread(client, (TATFTPWRQ) request, watcher);
					threads.add(thread);
					if (thread.connect(portNumber+1) > 0) {
						thread.run();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			response = request.getResponse();
		}
	}
}
