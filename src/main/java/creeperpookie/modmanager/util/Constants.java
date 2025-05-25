package creeperpookie.modmanager.util;

public class Constants
{
	public static final int PROTOCOL_VERSION = 1;
	public static final int DEFAULT_BUFFER_SIZE = 256000;
	public static final long RETRY_PACKET_READ_SLEEP_DELAY = 250;
	public static final long MAX_READ_TIMEOUT_MS = 5000;
	public static final byte[] FILE_FOUR_CC = new byte[] {'M', 'C', 'M', 'M'};
	public static final String HTTP_USER_AGENT = "ModManager/" + (double) PROTOCOL_VERSION;
}
