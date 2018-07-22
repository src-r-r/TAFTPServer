package io.gitub.srcrr.test;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

import io.github.srcrr.TATFTPACK;
import io.github.srcrr.TATFTPDATA;
import io.github.srcrr.TATFTPPacket;
import io.github.srcrr.TATFTPWRQ;

class TestPackage {

	@Test
	void testWRQPacketRead() {
		String sFilePath = "/path/to/some/file.txt", sMode = "netascii";
		int packetLength = TATFTPPacket.OPCODE_LENGTH + sFilePath.length() + 1 + sMode.length() + 1;
		System.out.println("Allocating " + packetLength + " bytes");
		byte[] data = ByteBuffer.allocate(packetLength+1)
				.putShort(TATFTPWRQ.OPCODE)
				.put(sFilePath.getBytes())
				.put((byte) 0)
				.put(sMode.getBytes())
				.put((byte) 0).array();
		TATFTPWRQ packet = null;
		try {
			packet = TATFTPWRQ.read(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(packet);
		assertEquals(packet.OPCODE, packet.getOpCode());
		assertEquals("/path/to/some/file.txt", packet.getFilenameAsString());
		assertEquals("netascii", packet.getModeAsString());
	}
	
	@Test
	void testWRQPacketToBytes() {
		String sFilePath = "/path/to/some/file.txt", sMode = "netascii";
		int packetLength = TATFTPPacket.OPCODE_LENGTH + sFilePath.length() + 1 + sMode.length() + 1;
		System.out.println("Allocating " + packetLength + " bytes");
		byte[] data = ByteBuffer.allocate(packetLength)
				.putShort(TATFTPWRQ.OPCODE)
				.put(sFilePath.getBytes())
				.put((byte) 0)
				.put(sMode.getBytes())
				.put((byte) 0).array();
		
		TATFTPWRQ pack = null;
		try {
			pack = new TATFTPWRQ(sFilePath, sMode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(pack);
		System.out.println("The packet: " + pack.toString());
		assertArrayEquals(data, pack.toBytes());
	}
	

	@Test
	void testDataPacketFullRead() {
		TATFTPDATA packet = null;
		byte[] data = new byte[TATFTPDATA.DATA_LENGTH];
		// Fill with non-null data
		for (int i = 0; i < TATFTPDATA.DATA_LENGTH; ++i) {
			data[i] = (byte) 1;
		}
		int packetLength = 2 + 2 + data.length + 1;
		short blockNumber = 12;
		System.out.println("Allocating " + packetLength + " bytes");
		byte[] allData = ByteBuffer.allocate(packetLength)
				.putShort(TATFTPDATA.OPCODE)
				.putShort(blockNumber)
				.put(data)
				.put((byte) 0).array();

		try {
			packet = TATFTPDATA.read(allData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(packet);
		assertEquals(blockNumber, packet.getBlockNumber());
		assertArrayEquals(data, packet.getData());
		assertFalse(packet.isLast());
	}
	
	@Test
	void testDataPacketPartialRead() {
		TATFTPDATA packet = null;
		byte[] data = new byte[TATFTPDATA.DATA_LENGTH / 2];
		// Fill with non-null data
		for (int i = 0; i < (TATFTPDATA.DATA_LENGTH / 2); ++i) {
			data[i] = (byte) 1;
		}
		int packetLength = 2 + 2 + data.length + 1;
		short blockNumber = 12;
		byte[] allData = ByteBuffer.allocate(packetLength)
				.putShort(TATFTPDATA.OPCODE)
				.putShort(blockNumber)
				.put(data)
				.put((byte) 0).array();

		try {
			packet = TATFTPDATA.read(allData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(packet);
		assertEquals(blockNumber, packet.getBlockNumber());
		assertArrayEquals(data, packet.getData());
		assertTrue(packet.isLast());
	}
	
	@Test
	void testDataPacketToBytes() {
		TATFTPDATA packet = null;
		byte[] data = new byte[TATFTPDATA.DATA_LENGTH];
		// Fill with non-null data
		for (int i = 0; i < TATFTPDATA.DATA_LENGTH; ++i) {
			data[i] = (byte) 1;
		}
		int packetLength = 2 + 2 + data.length + 1;
		short blockNumber = 12;
		System.out.println("Allocating " + packetLength + " bytes");
		byte[] allData = ByteBuffer.allocate(packetLength)
				.putShort(TATFTPDATA.OPCODE)
				.putShort(blockNumber)
				.put(data)
				.put((byte) 0).array();

		try {
			packet = new TATFTPDATA(blockNumber, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertArrayEquals(allData, packet.toBytes());
	}
	
	@Test
	void testAckPacketToBytes() {
		short blockNumber = 31;
		TATFTPACK packet = null;
		try {
			packet = new TATFTPACK(blockNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] allData = ByteBuffer.allocate(4)
				.putShort(TATFTPACK.OPCODE)
				.putShort(blockNumber).array();
		assertArrayEquals(allData, packet.toBytes());
	}
}
