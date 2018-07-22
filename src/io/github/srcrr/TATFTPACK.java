/**
 * 
 */
package io.github.srcrr;

import java.nio.ByteBuffer;

/**
 * @author jordan
 *
 */
public class TATFTPACK extends BNTAFTPPacket{

	public static final short OPCODE = 4;

	public TATFTPACK(short blockNumber) {
		super(OPCODE, blockNumber);
	}

	public TATFTPACK(int i) {
		this((short) i);
	}

	public static TATFTPACK read(byte [] data) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		short opcode = byteBuffer.getShort();
		short blockNumber = byteBuffer.getShort();
		return new TATFTPACK(blockNumber);
	}

	@Override
	public byte[] toBytes() {
		int capacity = 4;
		ByteBuffer buf = ByteBuffer.allocate(capacity)
				.putShort(getOpCode())
				.putShort(getBlockNumber());
		return buf.array();
	}

	@Override
	public TATFTPPacket getResponse() {
		// This is only sent by the server.
		return null;
	}
}
