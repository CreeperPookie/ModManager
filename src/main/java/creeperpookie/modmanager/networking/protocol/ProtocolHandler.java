package creeperpookie.modmanager.networking.protocol;

import creeperpookie.modmanager.ModManagerMod;
import creeperpookie.modmanager.networking.connection.ModManagerConnectionMeta;
import creeperpookie.modmanager.networking.connection.ModManagerConnectionResult;
import creeperpookie.modmanager.networking.protocol.impl.ProtocolV1Handler;
import creeperpookie.modmanager.util.exceptions.InvalidPacketException;
import net.minecraftforge.api.distmarker.Dist;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public interface ProtocolHandler
{
	/**
	 * Handles server data sent to the Mod Manager server.
	 *
	 * @param connectionMeta
	 * @param socket         the connected socket
	 * @param inputStream    the {@link DataInputStream DataInputStream} connected to the socket
	 * @param outputStream   the {@link DataOutputStream DataOutputStream} connected to the socket
	 * @param distSide       the side context to handle under
	 * @return the result of the packet data and the action that should be taken
	 * @throws IOException if a network error occurs
	 */
	default ModManagerConnectionResult handlePackets(ModManagerConnectionMeta connectionMeta, Socket socket, DataInputStream inputStream, DataOutputStream outputStream, Dist distSide) throws IOException
	{
		return switch (distSide)
		{
			case CLIENT ->
			{
				try
				{
					yield handleClient(connectionMeta, socket, inputStream, outputStream);
				}
				catch (NoSuchAlgorithmException | InvalidPacketException e)
				{
					yield ModManagerConnectionResult.CONNECTION_FAILED;
				}
			}
			case DEDICATED_SERVER -> handleServer(connectionMeta, socket, inputStream, outputStream);
		};
	}

	default void close(Socket socket, DataInputStream inputStream, DataOutputStream outputStream) throws IOException
	{
		socket.close();
		inputStream.close();
		outputStream.close();
	}

	ModManagerConnectionResult handleClient(ModManagerConnectionMeta connectionMeta, Socket socket, DataInputStream inputStream, DataOutputStream outputStream) throws IOException, NoSuchAlgorithmException, InvalidPacketException;
	ModManagerConnectionResult handleServer(ModManagerConnectionMeta connectionMeta, Socket socket, DataInputStream inputStream, DataOutputStream outputStream) throws IOException;


	static ProtocolHandler getVersionHandler(int protocolVersion)
	{
		return switch (protocolVersion)
		{
			case 1 -> new ProtocolV1Handler();
			default -> throw new UnsupportedOperationException("Protocol version " + protocolVersion + " is not currently supported");
		};
	}

	static boolean isVersionSupported(int protocolVersion)
	{
		try
		{
			Class.forName("creeperpookie." + ModManagerMod.MODID + ".networking.protocol.impl.ProtocolV" + protocolVersion + "Handler");
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
}
