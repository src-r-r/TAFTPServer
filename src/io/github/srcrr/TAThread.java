package io.github.srcrr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;

public class TAThread extends Thread {

	private final static int BUFFER_SIZE = 1024;

	private DatagramChannel mChannel = null;
	private SocketAddress mClient = null;
	private DatagramSocket mSocket = null;
	private TATFTPWRQ mWrq = null;
	private File mFile;
	private boolean mContinueWriting;

	private CloseWatcher mCloseWatcher;

	public TAThread(SocketAddress client, TATFTPWRQ initialRequest, CloseWatcher closeWatcher) throws IOException {
		mClient = client;
		mWrq = initialRequest;
		mChannel = DatagramChannel.open();
		mChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		mCloseWatcher = closeWatcher;
	}

	/**
	 * Try connecting to another port
	 * 
	 * @return Port number on which we're connected, or -1 if not found
	 */
	public int connect(int initialPort) {
		mSocket = mChannel.socket();

		for (int i = initialPort; i < 9999999; ++i) {
			try {
				System.err.println("(thread) Binding to port " + initialPort);
				mSocket.bind(new InetSocketAddress(initialPort));
			} catch (SocketException e) {
				continue;
			}
			if (mSocket.isBound()) {
				return mSocket.getLocalPort();
			}
		}

		return -1;
	}

	public void close() {
		mContinueWriting = false;
		mSocket.close();
		try {
			mChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCloseWatcher.onClose(this);
		System.err.println("Thread closing");
	}

	public void run() {
		TATFTPPacket request = null, response = null;
		TATFTPPacketFactory factory = TATFTPPacketFactory.getInstance();

		if (mSocket == null || !mSocket.isBound()) {
			try {
				throw new SocketException("Socket is not bound");
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// This is just a test

		// TATFTPERROR err = new TATFTPERROR(TATFTPERROR.ErrorCode.ILLEGAL_OPERATION,
		// "Illegal operation");
		// try {
		// err.sendTo(mChannel, mClient);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// close();

		mContinueWriting = true;

		// Handle the initial request
		String filename = mWrq.getFilenameAsString();
		System.err.println("Creating " + filename);
		mFile = new File(filename);

		if (!mFile.exists()) {
			try {
				mFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				response = new TATFTPERROR(TATFTPERROR.ErrorCode.ACCESS_VIOLATION,
						"Could not create file" + mWrq.getFilenameAsString());
				try {
					response.sendTo(mChannel, mClient);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					close();
				}
			}
		} else {
			// Clear the file of it's contents
			try {
				FileWriter writer = new FileWriter(mFile);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.err.println("created new file " + mWrq.getFilenameAsString());

		response = new TATFTPACK(0);
		try {
			response.sendTo(mChannel, mClient);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			close();
		}

		while (mContinueWriting) {
			ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
			System.err.println("Thread running...");
			buf.clear();
			try {
				mClient = mChannel.receive(buf);
				System.err.println("Received " + buf.array().length + " bytes");
			} catch (ClosedChannelException e2) {
				e2.printStackTrace();
				mCloseWatcher.onClose(this);
				return;
			} catch (IOException e6) {
				e6.printStackTrace();
				continue;
			}
			if (!buf.hasRemaining()) {
				System.err.println("none remaining");
				continue;
			}
			try {
				System.err.println("(thread) Reading request");
				request = factory.read(buf.array());
			} catch (TAException e2) {
				e2.printStackTrace();
				try {
					e2.asTATFTPERROR().sendTo(mChannel, mClient);
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} catch (Exception e4) {
					// TODO Auto-generated catch block
					e4.printStackTrace();
				}
				this.close();
			}

			// Handle all subsequent "DATA" requests
			if (request instanceof TATFTPDATA) {
				short bn = ((TATFTPDATA) request).getBlockNumber();
				System.err.println("Block #" + bn);
				try {
					FileWriter writer = new FileWriter(mFile, true);
					String data = ((TATFTPDATA) request).getDataAsString();
					System.err.println("Writing \"" + data + "\" to file");
					if (data.length() > 2)
						data = data.substring(0, data.length()-2);
					writer.write(data);
					writer.flush();
					writer.close();
					response = new TATFTPACK(bn);
					response.sendTo(mChannel, mClient);
				} catch (IOException e1) {
					e1.printStackTrace();
					response = new TATFTPERROR(TATFTPERROR.ErrorCode.FILE_NOT_FOUND, mWrq.getFilename());
					try {
						mChannel.send(ByteBuffer.wrap(response.toBytes()), mSocket.getRemoteSocketAddress());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				if (((TATFTPDATA) request).isLast()) {
					close();
				}
			}
		}
	}

}
