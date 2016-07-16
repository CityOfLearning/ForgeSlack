package com.derimagia.forgeslack.slack.commands;

import org.apache.commons.lang3.text.WordUtils;

import com.derimagia.forgeslack.handler.ConfigurationHandler;

import net.minecraft.server.MinecraftServer;

public class Whisper extends BaseSlackCommand {

	@Override
	public String getCommandName() {
		return "say";
	}

	@Override
	public String getCommandUsage() {
		return "say\tmessage a user directly. Example: $say ccoladmin1 hello";
	}

	@Override
	public void processCommand(String username, String[] args) {
		String user = args[0];
		String whisper = "";

		args = SlackCommandRegistry.dropFirstString(args);

		for (String arg : args) {
			whisper += arg + " ";
		}

		String message = String.format("/w %s " + ConfigurationHandler.playerSignature + " %s", user,
				WordUtils.capitalizeFully(username), whisper);
		message = ConfigurationHandler.formatColors(message);
		MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), message);
	}

}
