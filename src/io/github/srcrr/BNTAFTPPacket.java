package io.github.srcrr;

public abstract class BNTAFTPPacket extends TATFTPPacket {
	final static int BLOCK_NUMBER_LENGTH = 2;
	short mBlockNumber;
	
	public BNTAFTPPacket(short opcode, short blockNumber) {
		super(opcode);
		mBlockNumber = blockNumber;
	}
	
	public short getBlockNumber() {
		return mBlockNumber;
	}
}
