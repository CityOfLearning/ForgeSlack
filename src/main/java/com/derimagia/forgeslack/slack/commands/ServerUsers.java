package com.derimagia.forgeslack.slack.commands;

import java.util.List;

import com.derimagia.forgeslack.slack.SlackSender;

import net.minecraft.server.MinecraftServer;

public class ServerUsers extends BaseSlackCommand {

	@Override
	public List<String> getCommandAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "players";
	}

	@Override
	public String getCommandUsage() {
		return "players, reports number of players and their names";
	}

	@Override
	public void processCommand(String username, String[] args) {
		SlackSender.getInstance().send(
				String.format("Server has %d users logged on", MinecraftServer.getServer().getCurrentPlayerCount()),
				"Server");
		String users = "";
		for (String user : MinecraftServer.getServer().getAllUsernames()) {
			users += user + ", ";
		}
		SlackSender.getInstance().send("Usernames: " + users, "Server");
	}

}
