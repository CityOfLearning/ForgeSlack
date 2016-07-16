package com.derimagia.forgeslack.slack.commands;

import java.util.List;

import net.minecraft.command.CommandException;

public interface ISlackCommand extends Comparable<ISlackCommand> {
	List<String> getCommandAliases();

	/**
	 * Gets the name of the command
	 */
	String getCommandName();

	/**
	 * Gets the usage string for the command.
	 *
	 * @param sender
	 *            The command sender that executed the command
	 */
	String getCommandUsage();

	/**
	 * Callback when the command is invoked
	 *
	 * @param sender
	 *            The command sender that executed the command
	 * @param args
	 *            The arguments that were passed
	 */
	void processCommand(String username, String[] args) throws CommandException;
}