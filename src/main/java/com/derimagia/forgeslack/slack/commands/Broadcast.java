package com.derimagia.forgeslack.slack.commands;

import org.apache.commons.lang3.text.WordUtils;

import com.derimagia.forgeslack.handler.ConfigurationHandler;
import com.derimagia.forgeslack.slack.SlackSender;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class Broadcast extends BaseSlackCommand {

	@Override
	public String getCommandName() {
		return "talk";
	}

	@Override
	public String getCommandUsage() {
		return "talk\t <all|[user]> send global server messages or private messages. Example: $talk ccoladmin1 hello";
	}

	@Override
	public void processCommand(String username, String[] args) throws WrongUsageException {
		if (args.length > 0) {
			switch (args[0]) {
			case "all": {
				SlackCommandRegistry.dropFirstString(args);

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
				break;
			default: {
				String user = args[0];
				EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(user);

				if (player != null) {
					String whisper = "";

					args = SlackCommandRegistry.dropFirstString(args);

					for (String arg : args) {
						whisper += arg + " ";
					}

					String message = String.format("/w %s " + ConfigurationHandler.playerSignature + " %s", user,
							WordUtils.capitalizeFully(username), whisper);
					message = ConfigurationHandler.formatColors(message);
					MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
							message);
				} else {
					SlackSender.getInstance().send("Cannot find player", "Server");
				}
			}
				break;
			}
		} else {
			throw new WrongUsageException("", new Object[] {});
		}
	}

}
