package creeperpookie.modmanager;

import com.mojang.logging.LogUtils;
import creeperpookie.modmanager.proxies.ClientProxy;
import creeperpookie.modmanager.proxies.CommonProxy;
import creeperpookie.modmanager.proxies.ServerProxy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ModManagerMod.MODID)
public class ModManagerMod
{
	public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	public static final String MODID = "modmanager";
	private static final Logger LOGGER = LogUtils.getLogger();

	public ModManagerMod()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Register ourselves for server and other game events we are interested in
		modEventBus.addListener(this::onCommonSetup);
		modEventBus.addListener(this::onClientSetup);
		modEventBus.addListener(this::onServerSetup);
		modEventBus.addListener(this::onLoadComplete);

		// Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event)
	{
		proxy.onCommonSetup(event);
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event)
	{
		proxy.onClientSetup(event);
	}

	@SubscribeEvent
	public void onServerSetup(FMLDedicatedServerSetupEvent event)
	{
		proxy.onServerSetup(event);
	}

	@SubscribeEvent
	public void onLoadComplete(FMLLoadCompleteEvent event)
	{
		proxy.onLoadComplete(event);
	}

	public static Logger getLogger()
	{
		return LOGGER;
	}
}
