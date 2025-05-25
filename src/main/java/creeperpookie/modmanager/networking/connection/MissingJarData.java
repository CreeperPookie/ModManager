package creeperpookie.modmanager.networking.connection;

public class MissingJarData
{
	private String jarName;
	private boolean isURL;
	private byte[] data;

	public MissingJarData() {}

	public MissingJarData(String jarName, boolean isURL, byte[] data)
	{
		this.jarName = jarName;
		this.isURL = isURL;
		this.data = data;
	}

	public String getJarName()
	{
		return jarName;
	}

	public void setJarName(String jarName)
	{
		this.jarName = jarName;
	}

	public boolean isURL()
	{
		return isURL;
	}

	public void setIsURL(boolean isURL)
	{
		this.isURL = isURL;
	}

	public byte[] getData()
	{
		return data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}
}
