package com.derimagia.forgeslack.slack.commands;

import org.apache.commons.lang3.text.WordUtils;

import com.derimagia.forgeslack.handler.ConfigurationHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class Broadcast extends BaseSlackCommand {

	@Override
	public String getCommandName() {
		return "talk";
	}

	@Override
	public String getCommandUsage() {
		return "talk\tglobal server message";
	}

	@Override
	public void processCommand(String username, String[] args) {
		String msg = "";

		for (String arg : args) {
			msg += arg + " ";
		}

		String message = String.format("%s " + ConfigurationHandler.playerSignature + " %s",
				ConfigurationHandler.slackSignature, WordUtils.capitalizeFully(username), msg);
		message = ConfigurationHandler.formatColors(message);

		if (!username.isEmpty() && !(username.trim().equals("slackbot"))) {
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(message));
		}
	}

}
