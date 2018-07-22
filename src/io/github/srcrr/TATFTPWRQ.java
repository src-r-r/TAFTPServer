package io.github.srcrr;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TATFTPWRQ extends TATFTPPacket{
	public final static short OPCODE = 2;
	
	private final String MODE_NETASCII = "netascii", MODE_OCTET = "octet";
	byte[] mFilename = null;
	private byte[] mMode = null;
	private final static byte EOP = '\0';
	
	public TATFTPWRQ(byte[] filename, byte[] mode) {
		super(OPCODE);
		String sMode = new String(mode, StandardCharsets.UTF_8).toLowerCase();
//		if (!(sMode == MODE_NETASCII || sMode == MODE_OCTET)) {
//			throw new Exception("Invalid mode: " + sMode);
//		}
		mFilename = filename;
		mMode = mode;
	}
	
	public TATFTPWRQ(String filename, String mode) throws Exception {
		this(filename.getBytes(), mode.getBytes());
	}
	
	public static TATFTPWRQ read(byte [] data) {
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		short opcode = byteBuffer.getShort();
		
		byte [] filename = readFrom(byteBuffer);
		byte [] mode = readFrom(byteBuffer);
		
		System.out.println("opcode: " + opcode);
		System.out.println("Filename: " + new String(filename, StandardCharsets.UTF_8));
		System.out.println("Mode: \"" + new String(mode, StandardCharsets.UTF_8) + "\"");
		
		return new TATFTPWRQ(filename, mode);
	}
	
	public byte[] getFilename () {
		return mFilename;
	}
	
	public String getFilenameAsString() {
		return new String(this.getFilename(), StandardCharsets.UTF_8);
	}
	
	public byte[] getMode () {
		return mMode;
	}
	
	public String getModeAsString() {
		return new String(this.getMode(), StandardCharsets.UTF_8);
	}

	@Override
	public byte[] toBytes() {
		int capacity = 2 + mFilename.length + 1 + mMode.length + 1;
		ByteBuffer byteBuffer = ByteBuffer.allocate(capacity)
				.putShort(mOpCode)
				.put(mFilename)
				.put((byte) 0)
				.put(mMode)
				.put((byte) 0);
		return byteBuffer.array();
	}
	
	public String toString() {
		return new String(this.toBytes(), StandardCharsets.UTF_8);
	}

	@Override
	public TATFTPPacket getResponse() {
		return new TATFTPACK(0);
	}
}
