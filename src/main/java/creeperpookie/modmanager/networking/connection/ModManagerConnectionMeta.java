package creeperpookie.modmanager.networking.connection;

import creeperpookie.modmanager.ModManagerMod;
import creeperpookie.modmanager.util.ByteUtils;
import creeperpookie.modmanager.util.Constants;
import creeperpookie.modmanager.util.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * Represents the connection data to a Mod Manager Server.
 * @param ip the ip to connect to
 * @param port the port to connect to, as an unsigned short
 * @param key the secret key to connect with
 */
public record ModManagerConnectionMeta(String ip, int port, String key)
{
	/**
	 * Gets a ModManagerConnectionMeta instance from a .mcmm data file.
	 *
	 * @param dataFile the file input to read from
	 * @return a ModManagerConnectionMeta instance if the file is valid, otherwise null
	 */
	@Nullable
	public static ModManagerConnectionMeta getFromFile(@NotNull File dataFile)
	{
		if (!dataFile.getName().endsWith(".mcmm"))
		{
			ModManagerMod.getLogger().warn("Data file {} is not a mcmm file!!", dataFile.getAbsolutePath());
			return null;
		}
		else if (!dataFile.exists())
		{
			ModManagerMod.getLogger().warn("Data file {} does not exist!", dataFile.getAbsolutePath());
			return null;
		}
		else if (dataFile.length() == 0)
		{
			ModManagerMod.getLogger().warn("Data file {} is empty!", dataFile.getAbsolutePath());
			return null;
		}
		try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFile))))
		{
			if (!Arrays.equals(ByteUtils.readBytes(inputStream, 4), Constants.FILE_FOUR_CC))
			{
				ModManagerMod.getLogger().warn("Data file {} is not a Mod Manager data file!", dataFile.getAbsolutePath());
				return null;
			}
			int fileVersion = ByteUtils.readInt(inputStream);
			switch (fileVersion)
			{
				case 1 ->
				{
					String xorKey = Utility.swapCharacters(Utility.reverseString(ByteUtils.readString(inputStream)));
					String mainKey = Utility.xorString(Utility.swapCharacters(Utility.reverseString(ByteUtils.readString(inputStream))), xorKey);
					boolean isDomainName = ByteUtils.readByte(inputStream) == 1;
					String ip;
					if (!isDomainName)
					{
						byte[] ipBytes = ByteUtils.readBytes(inputStream, 4);
						for (int index = 0; index < ipBytes.length / 2; index++) // Reverse
						{
							byte temp = ipBytes[index];
							ipBytes[index] = ipBytes[ipBytes.length - index - 1];
							ipBytes[ipBytes.length - index - 1] = temp;
						}
						for (int index = 0; index < ipBytes.length; index += 2) // Swap
						{
							byte temp = ipBytes[index];
							ipBytes[index] = ipBytes[index + 1];
							ipBytes[index + 1] = temp;
						}
						String localXORKey = xorKey;
						while (localXORKey.length() < ipBytes.length) localXORKey += localXORKey;
						for (int index = 0; index < ipBytes.length; index++)
						{
							ipBytes[index] = (byte) (ipBytes[index] ^ xorKey.getBytes()[index]);
						}
						ip = Byte.toUnsignedInt(ipBytes[0]) + "." + Byte.toUnsignedInt(ipBytes[1]) + "." + Byte.toUnsignedInt(ipBytes[2]) + "." + Byte.toUnsignedInt(ipBytes[3]);
					}
					else ip = ByteUtils.readString(inputStream);
					return new ModManagerConnectionMeta(ip, Short.toUnsignedInt(ByteUtils.readShort(inputStream)), mainKey);
				}
				default ->
				{
					ModManagerMod.getLogger().warn("Data file version {} is not currently supported!", fileVersion);
					return null;
				}
			}
		}
		catch (IOException e)
		{
			ModManagerMod.getLogger().error("An error occurred whilst reading the data file {}!", dataFile.getAbsolutePath(), e);
			return null;
		}
	}

	/**
	 * Attempt to connect to the Mod Manager Server with the given data.
	 *
	 * @return the new {@link Socket Socket} if the connection was successful, otherwise null
	 */
	@NotNull
	public Socket connect() throws IOException
	{
		return new Socket(ip, port);
	}
}
