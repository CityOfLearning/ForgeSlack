package com.derimagia.forgeslack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.derimagia.forgeslack.handler.ConfigurationHandler;
import com.derimagia.forgeslack.handler.EventHandler;
import com.derimagia.forgeslack.slack.SlackReceiveServer;
import com.derimagia.forgeslack.slack.SlackSender;
import com.derimagia.forgeslack.slack.commands.SlackCommandRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author derimagia
 */
@Mod(modid = ForgeSlack.modId, name = ForgeSlack.modId, version = ForgeSlack.version, acceptableRemoteVersions = "*", dependencies = "required-after:dyn")
public class ForgeSlack {

	public static final String modId = "ForgeSlack";
	public static final String version = "0.1.0";

	public static final SlackCommandRegistry slackCommands = new SlackCommandRegistry();

	public static Logger log = LogManager.getLogger(modId);

	public static String getName(EntityPlayer player) {
		return player.getDisplayNameString();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (event.getSide() == Side.SERVER) {
			if (ConfigurationHandler.enabled) {
				ForgeSlack.log.info("Registering Event Handler");
				MinecraftForge.EVENT_BUS.register(new EventHandler());
			}
		}

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (event.getSide() == Side.SERVER) {
			ConfigurationHandler.init(event.getSuggestedConfigurationFile());
		}
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		if (event.getSide() == Side.SERVER) {
			ForgeSlack.log.info("Server Started");
			if (ConfigurationHandler.enabled) {
				new SlackReceiveServer();
				SlackSender.getInstance().sendServer("_Server is Online_");
			}
		}
	}

	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		if (event.getSide() == Side.SERVER) {
			ForgeSlack.log.info("Server Stopping");
			if (ConfigurationHandler.enabled) {
				SlackSender.getInstance().sendServer("_Server is Shutting Down_");
			}
		}
	}

}