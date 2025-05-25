package creeperpookie.modmanager.networking.protocol.impl;

import creeperpookie.modmanager.ModManagerMod;
import creeperpookie.modmanager.networking.PacketType;
import creeperpookie.modmanager.networking.connection.MissingJarData;
import creeperpookie.modmanager.networking.connection.ModManagerConnectionMeta;
import creeperpookie.modmanager.networking.connection.ModManagerConnectionResult;
import creeperpookie.modmanager.networking.protocol.ProtocolHandler;
import creeperpookie.modmanager.util.ByteUtils;
import creeperpookie.modmanager.util.Constants;
import creeperpookie.modmanager.util.Utility;
import creeperpookie.modmanager.util.exceptions.InvalidPacketException;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.CRC32C;

public class ProtocolV1Handler implements ProtocolHandler
{
	@Override
	public ModManagerConnectionResult handleClient(ModManagerConnectionMeta connectionMeta, Socket socket, DataInputStream inputStream, DataOutputStream outputStream) throws IOException, NoSuchAlgorithmException, InvalidPacketException
	{
		ModManagerConnectionResult result = null;
		ArrayList<MissingJarData> missingJars = new ArrayList<>();
		File mods = new File(Paths.get("").toAbsolutePath().toString(), "mods");
		ByteUtils.writeBytes(outputStream, PacketType.HELLO.createPacket(Constants.DEFAULT_BUFFER_SIZE));
		long authTime = System.currentTimeMillis();
		ByteUtils.writeBytes(outputStream, PacketType.AUTHENTICATION.createPacket(authTime));
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		ByteUtils.writeBytes(outputStream, PacketType.AUTHENTICATION.createPacket(sha256.digest((connectionMeta.key() + " " + authTime).getBytes())));
		if (PacketType.getPacketType(ByteUtils.readBytes(inputStream, 5)) != PacketType.SUCCESS && ByteUtils.readByte(inputStream) != (byte) 0xFF)
		{
			close(socket, inputStream, outputStream);
			result = ModManagerConnectionResult.CONNECTION_FAILED;
		}
		else
		{

			File[] modFiles = mods.listFiles((FilenameFilter) new SuffixFileFilter(".jar"));
			int modCount = mods.exists() && mods.isDirectory() && modFiles != null ? modFiles.length : 0;
			ByteUtils.writeBytes(outputStream, PacketType.CLIENT_MOD_HASHES.createPacket(modCount));
			CRC32C crc32C = new CRC32C();
			ModManagerMod.getLogger().info("Current directory: {}", Paths.get("").toAbsolutePath());
			if (modCount > 0) modLoop: for (File mod : modFiles)
			{
				if (mod.length() > 2147483647)
				{
					ModManagerMod.getLogger().warn("Mod file is over 2.1 GB, unable to check mod!");
					close(socket, inputStream, outputStream);
					result = ModManagerConnectionResult.CONNECTION_FAILED;
					break;
				}
				for (int retryCount = 0; retryCount < 5; retryCount++)
				{
					byte[] hash = Utility.getFileSHA256(mod, sha256);
					crc32C.update(hash);
					ByteUtils.writeBytes(outputStream, PacketType.CLIENT_MOD_HASHES.createPacket(hash));
					long recievedCRC32C = PacketType.CLIENT_MOD_HASHES.getLong(inputStream);
					if (crc32C.getValue() != recievedCRC32C) ByteUtils.writeBytes(outputStream, PacketType.CLIENT_MOD_RESEND.createPacket());
					else
					{
						ByteUtils.writeBytes(outputStream, PacketType.SUCCESS.createPacket((byte) 0x0));
						continue modLoop;
					}
				}
				ByteUtils.writeBytes(outputStream, PacketType.ERROR.createPacket((byte) 0x4));
				close(socket, inputStream, outputStream);
				result = ModManagerConnectionResult.CONNECTION_FAILED;
				break;
			}
			if (result == null)
			{
				byte[] header = ByteUtils.readBytes(inputStream, 5);
				if (PacketType.getPacketType(header) == PacketType.SUCCESS && ByteUtils.readByte(inputStream) == 0x1)
				{
					result = ModManagerConnectionResult.VALID;
				}
				else
				{
					if (PacketType.getPacketType(header) == PacketType.CLIENT_MISSING_MODS)
					{
						int missingModCount = ByteUtils.readInt(inputStream);
						for (int index = 0; index < missingModCount; index++)
						{
							String jarName = PacketType.CLIENT_MISSING_MODS.getString(inputStream);
							byte[] hash = PacketType.CLIENT_MISSING_MODS.getBytes(inputStream, 32);
							boolean isURL = PacketType.CLIENT_MISSING_MODS.getBoolean(inputStream);
							int dataSize = PacketType.CLIENT_MISSING_MODS.getInt(inputStream);
							ModManagerMod.getLogger().info("Downloading mod \"{}\" ({} byte{})...", jarName, dataSize, dataSize == 1 ? "" : "s");
							byte[] data = new byte[dataSize];
							byte[] jarData;
							for (int transferredBuffers = 0; transferredBuffers <= 32767; transferredBuffers++)
							{
								int bufferSize = PacketType.CLIENT_MISSING_MODS.getInt(inputStream);
								byte[] buffer = PacketType.CLIENT_MISSING_MODS.getBytes(inputStream, bufferSize);
								header = ByteUtils.readBytes(inputStream, 5);
								crc32C.reset();
								crc32C.update(buffer);
								ByteUtils.writeBytes(outputStream, PacketType.CLIENT_MISSING_MODS.createPacket(crc32C.getValue()));
								byte[] resendTypeHeader = ByteUtils.readBytes(inputStream, 5);
								if (PacketType.getPacketType(resendTypeHeader) == PacketType.CLIENT_MISSING_MODS_RESEND)
									continue;
								else if (PacketType.getPacketType(resendTypeHeader) == PacketType.SUCCESS && ByteUtils.readByte(inputStream) == (byte) 0x1)
									System.arraycopy(buffer, 0, data, transferredBuffers * bufferSize, bufferSize);
								if (PacketType.getPacketType(header) == PacketType.END_OF_FILE || PacketType.getPacketType(header) == PacketType.END_OF_BUFFER)
								{
									System.arraycopy(buffer, 0, data, transferredBuffers * bufferSize, bufferSize);
									if (PacketType.getPacketType(header) == PacketType.END_OF_FILE) break;
								}
							}
							missingJars.add(new MissingJarData(jarName, isURL, data));
						}
					}
					if (PacketType.getPacketType(header) == PacketType.CLIENT_EXTRA_MODS)
					{
						int extraModCount = PacketType.CLIENT_EXTRA_MODS.getInt(inputStream);
						ArrayList<byte[]> hashesToRemove = new ArrayList<>();
						for (int index = 0; index < extraModCount; index++)
						{
							for (int retryCount = 0; retryCount < 5; retryCount++)
							{
								byte[] hash = PacketType.CLIENT_EXTRA_MODS.getBytes(inputStream, 32);
								crc32C.update(hash);
								ByteUtils.writeBytes(outputStream, PacketType.CLIENT_EXTRA_MODS.createPacket(crc32C.getValue()));
								byte[] extraModsHeader = ByteUtils.readBytes(inputStream, 5);
								if (PacketType.getPacketType(extraModsHeader) == PacketType.SUCCESS && ByteUtils.readByte(inputStream) == 0x02)
								{
									hashesToRemove.add(extraModsHeader);
									break;
								}
								else if (PacketType.getPacketType(extraModsHeader) == PacketType.CLIENT_EXTRA_MODS_RESEND) continue;
								else if (PacketType.getPacketType(extraModsHeader) == PacketType.ERROR && ByteUtils.readByte(inputStream) == 0x06)
								{
									close(socket, inputStream, outputStream);
									result = ModManagerConnectionResult.CONNECTION_FAILED;
									break;
								}
							}
							if (result != null) break;
						}
						if (result == null)
						{
							ArrayList<File> filesToRemove = new ArrayList<>();
							for (File mod : modFiles)
							{
								if (mod.length() > 2147483647)
								{
									ModManagerMod.getLogger().warn("Mod file is over 2.1 GB, unable to check mod!");
									close(socket, inputStream, outputStream);
									result = ModManagerConnectionResult.CONNECTION_FAILED;
									break;
								}
								byte[] hash = Utility.getFileSHA256(mod, sha256);
								if (hashesToRemove.contains(hash)) filesToRemove.add(mod);
							}
							if (result == null)
							{
								while (!filesToRemove.isEmpty())
								{
									File removed = filesToRemove.remove(0);
									removed.renameTo(new File(removed.getName() + "_"));
								}
								result = ModManagerConnectionResult.MISSING_AND_EXTRA_MODS;
							}
						}
					}
					else
					{
						result = ModManagerConnectionResult.MISSING_MODS;
					}
				}
			}
		}
		missingJars.forEach(missingJarData ->
		{
			if (missingJarData.isURL())
			{
				try
				{
					HttpURLConnection connection = (HttpURLConnection) URI.create(new String(missingJarData.getData())).toURL().openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setRequestProperty("User-Agent", Constants.HTTP_USER_AGENT);
					connection.setDoOutput(true);
					connection.setInstanceFollowRedirects(true);
					connection.connect();
					DataInputStream jarInputStream = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
					byte[] jarData = new byte[connection.getContentLength()];
					byte[] buf;
					int bufferCount = 0;
					while ((buf = ByteUtils.readBytes(jarInputStream, 1024)).length > 0)
					{
						System.arraycopy(buf, 0, jarData, bufferCount * buf.length, buf.length);
						bufferCount++;
					}
					connection.disconnect();
					missingJarData.setData(jarData);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
			try
			{
				File modFile = new File(mods, missingJarData.getJarName());
				DataOutputStream fileStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(modFile)));
				fileStream.write(missingJarData.getData());
				fileStream.flush();
				fileStream.close();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	@Override
	public ModManagerConnectionResult handleServer(ModManagerConnectionMeta connectionMeta, Socket socket, DataInputStream inputStream, DataOutputStream outputStream) throws IOException
	{
		return ModManagerConnectionResult.CONNECTION_FAILED; // TODO add server communication
	}
}
