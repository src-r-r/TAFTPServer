package io.github.srcrr;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.LinkedList;

public abstract class TATFTPPacket {
	
	public static final int OPCODE_LENGTH = 2;
	protected short mOpCode = 0;
	
	/**
	 * Get a straight-up byte array from the buffer.
	 * Reads each byte from the ByteBuffer until null (0) is encountered.
	 * @param byteBuffer
	 * @return A byte array from byteBuffer
	 */
	protected static byte [] readFrom(ByteBuffer byteBuffer) {
		LinkedList<Byte> bytes = new LinkedList<Byte>();
		byte b = byteBuffer.get();
		while (b != ((byte) 0)) {
			bytes.add(b);
			b = byteBuffer.get();
		}
		byte [] out = new byte [bytes.size()];
		for (int i = 0; i < bytes.size(); ++i) {
			out[i] = bytes.get(i).byteValue();
		}
		return out;
	}

	protected TATFTPPacket(short opcode) {
		mOpCode = opcode;
	}
	
	public short getOpCode() {
		return mOpCode;
	}
	
	public abstract byte [] toBytes();

	public abstract TATFTPPacket getResponse();
	
	public DatagramPacket asDatagramPacket() {
		byte [] data = this.toBytes();
		return data == null ? null : new DatagramPacket(data, data.length);
	}
	
	public void sendTo(DatagramSocket socket) throws IOException {
		DatagramPacket packet = asDatagramPacket();
		if (packet == null)
			return;
		socket.send(packet);
	}
	
	public void sendTo(DatagramChannel server, SocketAddress mClient) throws IOException {
		ByteBuffer buf = ByteBuffer.wrap(this.toBytes());
		server.send(buf, mClient);	
	}
}
