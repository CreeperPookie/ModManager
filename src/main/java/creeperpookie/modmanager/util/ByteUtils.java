package creeperpookie.modmanager.util;

import creeperpookie.modmanager.ModManagerMod;
import creeperpookie.modmanager.util.Constants;
import creeperpookie.modmanager.util.exceptions.InvalidStreamException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;

public class ByteUtils
{
	private static boolean validateStream(DataInputStream inputStream) throws IOException
	{
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime <= Constants.MAX_READ_TIMEOUT_MS)
		{
			try
			{
				Thread.sleep(Constants.RETRY_PACKET_READ_SLEEP_DELAY);
				if (inputStream.available() > 0) break;
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
		if (inputStream.available() <= 0) throw new InvalidStreamException("Provided input stream does not have any more data");
		return inputStream.available() > 0;
	}

	private static boolean validateStream(DataInputStream inputStream, int bytesRequired) throws IOException
	{
		if (bytesRequired < 1) throw new IllegalArgumentException("Not enough bytes were requested to validate");
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime <= Constants.MAX_READ_TIMEOUT_MS)
		{
			try
			{
				Thread.sleep(Constants.RETRY_PACKET_READ_SLEEP_DELAY);
				if (inputStream.available() >= bytesRequired) break;
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
		if (inputStream.available() <= 0) throw new InvalidStreamException("Provided input stream does not have any more data");
		else return inputStream.available() >= bytesRequired;
	}

	public static boolean readBoolean(DataInputStream inputStream) throws IOException
	{
		validateStream(inputStream, Byte.BYTES);
		boolean value = inputStream.readBoolean();
		ModManagerMod.getLogger().info("Read boolean {}", value);
		return value;
	}

	public static byte readByte(DataInputStream inputStream) throws IOException
	{
		validateStream(inputStream, Byte.BYTES);
		byte value = inputStream.readByte();
		ModManagerMod.getLogger().info("Read byte {}", value);
		return value;
	}

	public static byte[] readBytes(@NotNull DataInputStream inputStream, int bytesToRead) throws IOException
	{
		validateStream(inputStream, bytesToRead);
		byte[] value = inputStream.readNBytes(bytesToRead);
		ModManagerMod.getLogger().info("Reading {} + byte{}{}", value.length, value.length == 1 ? "" : "s", value.length < 256 ? " " + Utility.byteArrayToHex(true, value) : "");
		return value;
	}

	public static char readChar(DataInputStream inputStream) throws IOException
	{
		validateStream(inputStream, Character.BYTES);
		char value = inputStream.readChar();
		ModManagerMod.getLogger().info("Read char {}", value);
		return value;
	}

	public static short readShort(DataInputStream inputStream) throws IOException
	{
		validateStream(inputStream, Short.BYTES);
		short value = inputStream.readShort();
		ModManagerMod.getLogger().info("Read short {}", value);
		return value;
	}

	public static int readInt(@NotNull DataInputStream inputStream) throws IOException
	{
		validateStream(inputStream, Integer.BYTES);
		int value = inputStream.readInt();
		ModManagerMod.getLogger().info("Read int {}", value);
		return value;
	}

	public static long readLong(@NotNull DataInputStream inputStream) throws IOException
	{
		validateStream(inputStream, Long.BYTES);
		long value = inputStream.readLong();
		ModManagerMod.getLogger().info("Read long {}", value);
		return value;
	}

	@NotNull
	public static String readString(@NotNull DataInputStream inputStream) throws IOException
	{
		int length = readInt(inputStream);
		ModManagerMod.getLogger().info("Read string length {}", length);
		validateStream(inputStream, length);
		String value = new String(readBytes(inputStream, length));
		ModManagerMod.getLogger().info("Read string {}", value);
		return value;
	}

	public static void writeBoolean(DataOutputStream outputStream, boolean value) throws IOException
	{
		ModManagerMod.getLogger().info("Writing boolean {}", value);
		outputStream.writeBoolean(value);
		outputStream.flush();
	}

	public static void writeByte(@NotNull DataOutputStream outputStream, byte value) throws IOException
	{
		ModManagerMod.getLogger().info("Writing byte {}", value);
		outputStream.write(value);
		outputStream.flush();
	}

	public static void writeBytes(@NotNull DataOutputStream outputStream, byte... value) throws IOException
	{
		ModManagerMod.getLogger().info("Writing {} + byte{}{}", value.length, value.length == 1 ? "" : "s", value.length < 256 ? " " + Utility.byteArrayToHex(true, value) : "");
		outputStream.write(value);
		outputStream.flush();
	}

	public static void writeChar(@NotNull DataOutputStream outputStream, char value) throws IOException
	{
		ModManagerMod.getLogger().info("Writing char {}", value);
		outputStream.writeChar(value);
		outputStream.flush();
	}

	public static void writeShort(@NotNull DataOutputStream outputStream, short value) throws IOException
	{
		ModManagerMod.getLogger().info("Writing short {}", value);
		outputStream.writeShort(value);
		outputStream.flush();
	}

	public static void writeInt(@NotNull DataOutputStream outputStream, int value) throws IOException
	{
		ModManagerMod.getLogger().info("Writing int {}", value);
		outputStream.writeInt(value);
		outputStream.flush();
	}

	public static void writeLong(@NotNull DataOutputStream outputStream, long value) throws IOException
	{
		ModManagerMod.getLogger().info("Writing long {}", value);
		outputStream.writeLong(value);
		outputStream.flush();
	}

	public static void writeString(@NotNull DataOutputStream outputStream, @NotNull String value) throws IOException
	{
		ModManagerMod.getLogger().info("Writing string length {}", value.length());
		outputStream.writeInt(value.length());
		ModManagerMod.getLogger().info("Writing string {}", value);
		outputStream.write(value.getBytes());
		outputStream.flush();
	}
}
