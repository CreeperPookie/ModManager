package creeperpookie.modmanager.networking;

import creeperpookie.modmanager.util.ByteUtils;
import creeperpookie.modmanager.util.Utility;
import creeperpookie.modmanager.util.exceptions.InvalidPacketException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum PacketType
{
	HELLO,
	AUTHENTICATION,
	CLIENT_MOD_HASHES,
	CLIENT_MISSING_MODS,
	CLIENT_EXTRA_MODS,
	CLIENT_MOD_RESEND,
	CLIENT_MISSING_MODS_RESEND,
	CLIENT_EXTRA_MODS_RESEND,
	CRC_MATCH,
	CRC_MISMATCH,
	END_OF_FILE,
	END_OF_BUFFER,
	ERROR,
	SUCCESS,
	@ApiStatus.Internal MAX_PACKET_VALUE;

	private static final byte[] BASE_HEADER = new byte[]{'M', 'C', 'M', 'M'};

	@Nullable
	public static PacketType getPacketType(byte[] header)
	{
		if (header[4] < 0 || header[4] >= MAX_PACKET_VALUE.ordinal() || !Utility.arrayContainsAll(header, BASE_HEADER))
		{
			return null; // not a valid packet
		}
		else return PacketType.values()[header[4]];
	}

	private String getName()
	{
		return Utility.formatText(name());
	}

	public boolean isHeaderInvalid(byte[] headerInput)
	{
		byte[] header = getHeader();
		if (headerInput.length < header.length) return true;
		else return !Utility.arrayContainsAll(headerInput, header);
	}
	
	public byte[] getHeader()
	{
		byte[] header = new byte[5];
		System.arraycopy(BASE_HEADER, 0, header, 0, 4);
		header[4] = (byte) ordinal();
		return header;
	}

	public boolean getBoolean(DataInputStream inputStream) throws IOException, InvalidPacketException
	{
		return getByte(inputStream) == 0x1;
	}

	public byte getByte(DataInputStream inputStream) throws IOException, InvalidPacketException
	{
		if (isHeaderInvalid(ByteUtils.readBytes(inputStream, 5))) throw new InvalidPacketException("Packet is not a " + getName() + " packet");
		else return ByteUtils.readByte(inputStream);
	}

	public byte[] getBytes(DataInputStream inputStream, int byteCount) throws IOException, InvalidPacketException
	{
		if (isHeaderInvalid(ByteUtils.readBytes(inputStream, 5))) throw new InvalidPacketException("Packet is not a " + getName() + " packet");
		else return ByteUtils.readBytes(inputStream, byteCount);
	}

	public char getChar(DataInputStream inputStream) throws IOException, InvalidPacketException
	{
		if (isHeaderInvalid(ByteUtils.readBytes(inputStream, 5))) throw new InvalidPacketException("Packet is not a " + getName() + " packet");
		else return ByteUtils.readChar(inputStream);
	}

	public short getShort(DataInputStream inputStream) throws IOException, InvalidPacketException
	{
		if (isHeaderInvalid(ByteUtils.readBytes(inputStream, 5))) throw new InvalidPacketException("Packet is not a " + getName() + " packet");
		else return ByteUtils.readShort(inputStream);
	}

	public int getInt(DataInputStream inputStream) throws IOException, InvalidPacketException
	{
		if (isHeaderInvalid(ByteUtils.readBytes(inputStream, 5))) throw new InvalidPacketException("Packet is not a " + getName() + " packet");
		else return ByteUtils.readInt(inputStream);
	}

	public long getLong(DataInputStream inputStream) throws IOException, InvalidPacketException
	{
		if (isHeaderInvalid(ByteUtils.readBytes(inputStream, 5))) throw new InvalidPacketException("Packet is not a " + getName() + " packet");
		else return ByteUtils.readLong(inputStream);
	}

	public String getString(DataInputStream inputStream) throws IOException, InvalidPacketException
	{
		if (isHeaderInvalid(ByteUtils.readBytes(inputStream, 5))) throw new InvalidPacketException("Packet is not a " + getName() + " packet");
		else return new String(ByteUtils.readBytes(inputStream, ByteUtils.readInt(inputStream)));
	}
	
	public byte[] getPacketData(byte... packet) throws InvalidPacketException
	{
		byte[] header = getHeader();
		if (!Utility.arrayContainsAll(packet, header)) throw new InvalidPacketException("Packet is not a " + getName() + " packet");
		byte[] data = new byte[packet.length - header.length];
		System.arraycopy(packet, header.length, data, 0, data.length);
		return data;
	}

	public byte[] createPacket() 
	{
		return createPacket(new byte[0]);
	}

	public byte[] createPacket(boolean data)
	{
		return createPacket((byte) (data ? 0x01 : 0x00));
	}

	public byte[] createPacket(byte data)
	{
		byte[] header = getHeader();
		byte[] packet = new byte[header.length + Byte.BYTES];
		System.arraycopy(header, 0, packet, 0, header.length);
		header[header.length - 1] = data;
		return packet;
	}
	
	public byte[] createPacket(byte... data)
	{
		byte[] header = getHeader();
		if (data != null && data.length != 0)
		{
			byte[] packet = new byte[header.length + data.length];
			System.arraycopy(header, 0, packet, 0, header.length);
			System.arraycopy(data, 0, packet, header.length, data.length);
			return packet;
		}
		return header;
	}
	
	public byte[] createPacket(char data)
	{
		byte[] header = getHeader();
		byte[] packet = new byte[header.length + Character.BYTES];
		System.arraycopy(header, 0, packet, 0, header.length);
		byte[] charData = new byte[Character.BYTES];
		ByteBuffer.wrap(charData).putChar(data);
		System.arraycopy(charData, 0, packet, header.length, Character.BYTES);
		return packet;
	}

	public byte[] createPacket(short data)
	{
		byte[] header = getHeader();
		byte[] packet = new byte[header.length + Short.BYTES];
		System.arraycopy(header, 0, packet, 0, header.length);
		byte[] shortData = new byte[Short.BYTES];
		ByteBuffer.wrap(shortData).putShort(data);
		System.arraycopy(shortData, 0, packet, header.length, Short.BYTES);
		return packet;
	}

	public byte[] createPacket(int data)
	{
		byte[] header = getHeader();
		byte[] packet = new byte[header.length + Integer.BYTES];
		System.arraycopy(header, 0, packet, 0, header.length);
		byte[] intData = new byte[Integer.BYTES];
		ByteBuffer.wrap(intData).putInt(data);
		System.arraycopy(intData, 0, packet, header.length, Integer.BYTES);
		return packet;
	}

	public byte[] createPacket(long data)
	{
		byte[] header = getHeader();
		byte[] packet = new byte[header.length + Long.BYTES];
		System.arraycopy(header, 0, packet, 0, header.length);
		byte[] longData = new byte[Long.BYTES];
		ByteBuffer.wrap(longData).putLong(data);
		System.arraycopy(longData, 0, packet, header.length, Long.BYTES);
		return packet;
	}

	public byte[] createPacket(String data)
	{
		byte[] header = getHeader();
		byte[] packet = new byte[header.length + (Byte.BYTES * data.length())];
		System.arraycopy(header, 0, packet, 0, header.length);
		System.arraycopy(data.getBytes(), 0, packet, header.length, data.length());
		return packet;
	}
}
