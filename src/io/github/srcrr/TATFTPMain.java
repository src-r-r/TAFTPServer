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
	
	private final static int BUFFER_SIZE = 1024;

	public TATFTPMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		DatagramChannel primaryChannel = null;
		TATFTPPacket request = null, response = null;
		ArrayList<TAThread> threads = new ArrayList<TAThread>();
		CloseWatcher watcher = new CloseWatcher(threads);
		
		TATFTPPacketFactory factory = TATFTPPacketFactory.getInstance();
		
		try {
			primaryChannel = DatagramChannel.open();
			primaryChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DatagramSocket primarySocket = primaryChannel.socket();
		int boundPort = 69;
		for (int i = 69; (i < 999999) && (!primarySocket.isBound()); ++i) {
			try {
//				System.out.println("binding to port " + i);
				primarySocket.bind(new InetSocketAddress(i));
				boundPort = i;
			} catch (SocketException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
		System.out.println("Bound to port " + boundPort);
		if (!primarySocket.isBound()) {
			throw new RuntimeException("Could not bind to any port!");
		}
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
		while (primarySocket.isBound()) {
			System.err.println("waiting for data");
			buf.clear();
			SocketAddress client = null;
			try {
				client = primaryChannel.receive(buf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!buf.hasRemaining()) { // i.e. It's empty
				System.err.println("It's empty.");
				response = new TATFTPERROR(TATFTPERROR.ErrorCode.NOT_DEFINED, "Empty data");
				try {
					primaryChannel.send(ByteBuffer.wrap(response.toBytes()), primaryChannel.getRemoteAddress());
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
					e1.asTATFTPERROR().sendTo(primarySocket);
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
				DatagramChannel channel;
				try {
					TAThread thread = new TAThread(client, (TATFTPWRQ) request, watcher);
					threads.add(thread);
					if (thread.connect(boundPort+1) > 0) {
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
