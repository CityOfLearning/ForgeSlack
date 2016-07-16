package com.derimagia.forgeslack.slack.commands;

import java.util.List;

import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class DoMcCommand extends BaseSlackCommand {

	@Override
	public String getCommandName() {
		return "cmd";
	}

	@Override
	public String getCommandUsage() {
		return "cmd, execute a console command from minecraft";
	}

	@Override
	public void processCommand(String username, String[] args) throws WrongUsageException {
		String mcCmd = "";

		for (String arg : args) {
			mcCmd += arg + " ";
		}

		if (MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), mcCmd) < 0) {
			throw new WrongUsageException(getCommandUsage(), new Object[0]);
		}
	}

}
