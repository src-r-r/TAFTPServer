/**
 * 
 */
package io.github.srcrr;

import java.nio.ByteBuffer;

/**
 * @author jordan
 *
 */
public class TATFTPERROR extends TATFTPPacket {
	
	public final static int ERROR_NUMBER_LENGTH = 2;

	public static final short OPCODE = 5;
	
	public enum ErrorCode {
		// Thanks https://stackoverflow.com/a/1067371
		// and https://lstu.fr/H-anN5ZU
		NOT_DEFINED(0),
		FILE_NOT_FOUND(1),
		ACCESS_VIOLATION(2),
		DISK_FULL(3),
		ILLEGAL_OPERATION(4),
		UNKONWN_TRANSFER_ID(5),
		FILE_ALREADY_EXISTS(6),
		NO_SUCH_USER(7);

	    private final short code;
	    ErrorCode(int code) { this.code = (short) code; }
	    public short getValue() { return code; }
	};
	
	private short mErrorNumber;

	private byte[] mErrorMessage = null;

	public TATFTPERROR(short errorNumber, byte [] errorMessage) {
		super(OPCODE);
		mErrorNumber = errorNumber; 
		mErrorMessage = errorMessage;
	}
	
	public TATFTPERROR(short errorNumber, String errorMessage) {
		this(errorNumber, (errorMessage == null ? null : errorMessage.getBytes()));
	}

	public TATFTPERROR(ErrorCode mErrorCode, String errorMessage) {
		this(mErrorCode.getValue(), errorMessage);
	}

	public TATFTPERROR(ErrorCode mErrorCode, byte [] errorMessage) {
		this(mErrorCode.getValue(), errorMessage);
	}

	public static TATFTPPacket read(byte[] data) {
		// TODO Auto-generated method stub
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		short opcode = byteBuffer.getShort();
		short errorNumber = byteBuffer.getShort();
		byte[] errorMessage = readFrom(byteBuffer);
		return new TATFTPERROR(errorNumber, errorMessage);
	}

	@Override
	public byte[] toBytes() {
		int capacity = 4 + mErrorMessage.length + 1;
		ByteBuffer buf = ByteBuffer.allocate(capacity)
				.putShort(getOpCode())
				.putShort(mErrorNumber)
				.put(mErrorMessage)
				.put((byte) 0);
		return buf.array();
	}
	
	public short getErrorNumber(short errorNumber) {
		return mErrorNumber;
	}

	@Override
	public TATFTPPacket getResponse() {
		// This is only sent by the server.
		return null;
	}
}
