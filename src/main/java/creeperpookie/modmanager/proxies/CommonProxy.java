package creeperpookie.modmanager.proxies;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class CommonProxy
{
	public void onCommonSetup(FMLCommonSetupEvent event)
	{

	}

	public void onClientSetup(FMLClientSetupEvent event)
	{
		// client only, do not put code in here in the common proxy!
	}

	public void onServerSetup(FMLDedicatedServerSetupEvent event)
	{
		// server only, do not put code in here in the common proxy!
	}

	public void onLoadComplete(FMLLoadCompleteEvent event)
	{

	}
}
