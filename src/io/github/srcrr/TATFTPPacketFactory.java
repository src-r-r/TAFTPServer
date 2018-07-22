package io.github.srcrr;

import java.nio.ByteBuffer;

public class TATFTPPacketFactory {
	
	private static TATFTPPacketFactory mInstance = null;

	private TATFTPPacketFactory() {
	}
	
	public static TATFTPPacketFactory getInstance() {
		if (mInstance == null) {
			mInstance = new TATFTPPacketFactory();
		}
		return mInstance;
	}

	public TATFTPPacket read(byte [] data) throws TAException {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		short opcode = buffer.getShort();
		System.err.println("opcode: " + opcode);
		switch (opcode) {
			case TATFTPWRQ.OPCODE:
				System.err.println("WRQ");
				return TATFTPWRQ.read(data);
			case TATFTPDATA.OPCODE:
				System.err.println("DATA");
				return TATFTPDATA.read(data);
			case TATFTPACK.OPCODE:
				System.err.println("ACK");
				return TATFTPACK.read(data);
			case TATFTPERROR.OPCODE:
				System.err.println("ERROR");
				return TATFTPERROR.read(data);
			default:
				System.err.println("Huh? I don't know the opcode " + opcode);
				throw new TAException(TATFTPERROR.ErrorCode.NOT_DEFINED, "opcode: " + String.valueOf(opcode));
		}
	}
}
