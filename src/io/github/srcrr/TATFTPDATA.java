package io.github.srcrr;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TATFTPDATA extends BNTAFTPPacket {
	
	public final static int DATA_LENGTH = 512;

	public static final short OPCODE = 3;
	
	private byte [] mData = null;

	public TATFTPDATA(short blockNumber, byte [] data) {
		super(OPCODE, blockNumber);
		int dataLen = 0; // the "real" data length
		for (int i = 0; (i < data.length) && (data[i] != (byte) 0) ; ++i) {
			dataLen = i+1;
		}
		mData = new byte[dataLen];
		for (int i = 0; i < dataLen; ++i) {
			mData[i] = data[i];
		}
		System.err.println("(stored) data as string: " + getDataAsString());
	}
	
	public TATFTPDATA(short blockNumber, String data) throws Exception {
		this(blockNumber, data.getBytes());
	}

	/**
	 * True if this data packet is the last packet.
	 * @return true if this data packet is the last packet in a series, false otherwise.
	 */
	public boolean isLast() {
		return mData.length < DATA_LENGTH;
	}
	
	public static TATFTPDATA read(byte [] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);

		short opCode = buf.getShort();
		short blockNumber = buf.getShort();
		byte [] intData = readFrom(buf);
		System.out.println("internal data: "
				+ new String(intData, StandardCharsets.UTF_8));
		
		return new TATFTPDATA(blockNumber, intData);
	}

	@Override
	public byte[] toBytes() {
		int capacity = 4 + mData.length + 1;
		ByteBuffer buf = ByteBuffer.allocate(capacity)
				.putShort(getOpCode())
				.putShort(mBlockNumber)
				.put(mData);
		return buf.array();
	}

	public byte [] getData() {
		return mData;
	}
	
	public String getDataAsString() {
		return new String(this.mData, StandardCharsets.UTF_8);
	}

	@Override
	public TATFTPPacket getResponse() {
		return new TATFTPACK(mBlockNumber);
	}
}
