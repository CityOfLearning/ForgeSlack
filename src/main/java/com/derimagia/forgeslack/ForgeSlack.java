package com.derimagia.forgeslack;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.derimagia.forgeslack.handler.ConfigurationHandler;
import com.derimagia.forgeslack.handler.EventHandler;
import com.derimagia.forgeslack.slack.SlackReceiveServer;
import com.derimagia.forgeslack.slack.SlackSender;
import com.dyn.utils.CCOLPlayerInfo;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

/**
 * @author derimagia
 */
@Mod(modid = ForgeSlack.modId, name = ForgeSlack.modId, version = ForgeSlack.version, dependencies = "required-after:dyn")
public class ForgeSlack {

	public static final String modId = "ForgeSlack";
	public static final String version = "0.1.0";

	public static Logger log = LogManager.getLogger(modId);
	
	public static Map<String, CCOLPlayerInfo> playerInfo = new HashMap<String, CCOLPlayerInfo>();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());

		if (ConfigurationHandler.enabled) {
			MinecraftForge.EVENT_BUS.register(new EventHandler());
		}
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		if (ConfigurationHandler.enabled) {
			new SlackReceiveServer();
			SlackSender.getInstance().send("_Server is Online_", "Server");
		}
	}

	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		if (ConfigurationHandler.enabled) {
			new SlackReceiveServer();
			SlackSender.getInstance().send("_Server is Shutting Down_", "Server");
		}
	}

}