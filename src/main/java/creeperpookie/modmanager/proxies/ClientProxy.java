package creeperpookie.modmanager.proxies;

import creeperpookie.modmanager.ModManagerMod;
import creeperpookie.modmanager.networking.PacketType;
import creeperpookie.modmanager.networking.connection.ModManagerConnectionMeta;
import creeperpookie.modmanager.networking.connection.ModManagerConnectionResult;
import creeperpookie.modmanager.networking.protocol.ProtocolHandler;
import creeperpookie.modmanager.util.ByteUtils;
import creeperpookie.modmanager.util.Constants;
import creeperpookie.modmanager.util.exceptions.InvalidPacketException;
import creeperpookie.modmanager.util.exceptions.InvalidStreamException;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.targets.FMLServerLaunchHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;

public class ClientProxy extends CommonProxy
{
	@Override
	public void onCommonSetup(FMLCommonSetupEvent event)
	{
		super.onCommonSetup(event);
		ModManagerConnectionResult result = ModManagerConnectionResult.CONNECTION_FAILED;
		ModManagerConnectionMeta connectionMeta = ModManagerConnectionMeta.getFromFile(new File(Paths.get("").toAbsolutePath().toString(), "data.mcmm"));
		if (connectionMeta != null)
		{
			try (Socket socket = connectionMeta.connect())
			{
				DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				if (PacketType.HELLO.isHeaderInvalid(ByteUtils.readBytes(inputStream, 5)))
				{
					ByteUtils.writeBytes(outputStream, PacketType.ERROR.createPacket((byte) 0x0));
					socket.close();
					inputStream.close();
					outputStream.close();
				}
				else
				{
					ByteUtils.writeBytes(outputStream, PacketType.HELLO.createPacket());
					int protocolVersion = PacketType.HELLO.getInt(inputStream);
					if (ProtocolHandler.isVersionSupported(protocolVersion))
					{
						ByteUtils.writeBytes(outputStream, PacketType.HELLO.createPacket(protocolVersion));
						result = ProtocolHandler.getVersionHandler(protocolVersion).handlePackets(connectionMeta, socket, inputStream, outputStream, Dist.CLIENT);
					}
					else
					{
						ByteUtils.writeBytes(outputStream, PacketType.HELLO.createPacket(Constants.PROTOCOL_VERSION));
						socket.close();
						inputStream.close();
						outputStream.close();
					}
				}
			}
			catch (IOException | InvalidPacketException | InvalidStreamException e)
			{
				ModManagerMod.getLogger().warn("An error occurred whilst connecting to Mod Manager server!", e);
			}
		}
		if (result == ModManagerConnectionResult.CONNECTION_FAILED) ModManagerMod.getLogger().warn("Failed to connect to Mod Manager server; mods may be out of date!");
		else if (result == ModManagerConnectionResult.VALID) ModManagerMod.getLogger().info("Successfully validated current mods!");
		else
		{
			ModManagerMod.getLogger().warn("Mod Manager updated mods, stopping client;");
			ModManagerMod.getLogger().warn("The client will need to be manually started!");
			Minecraft.getInstance().close();
		}
	}
}
