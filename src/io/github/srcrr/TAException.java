package io.github.srcrr;

public class TAException extends Exception {
	
	TATFTPERROR.ErrorCode mErrorCode = null;
	String mMessage = null;

	public TAException(TATFTPERROR.ErrorCode errorCode) {
		mErrorCode = errorCode;
	}

	public TAException(TATFTPERROR.ErrorCode errorCode, String message) {
		super(message);
		mErrorCode = errorCode;
	}

	public TAException(TATFTPERROR.ErrorCode errorCode, Throwable cause) {
		super(cause);
		mErrorCode = errorCode;
	}

	public TAException(TATFTPERROR.ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		mErrorCode = errorCode;
	}

	public TAException(TATFTPERROR.ErrorCode errorCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		mErrorCode = errorCode;
	}

	public TATFTPERROR asTATFTPERROR() throws Exception {
		return new TATFTPERROR(mErrorCode, mMessage);
	}
}
