package com.derimagia.forgeslack.slack.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.derimagia.forgeslack.slack.SlackSender;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class SlackCommandRegistry {

	/**
	 * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n
	 * elements)
	 */
	public static String[] dropFirstString(String[] input) {
		String[] astring = new String[input.length - 1];
		System.arraycopy(input, 1, astring, 0, input.length - 1);
		return astring;
	}

	private final Map<String, ISlackCommand> commandMap = Maps.<String, ISlackCommand> newHashMap();

	private final Set<ISlackCommand> commandSet = Sets.<ISlackCommand> newHashSet();

	public boolean executeCommand(String sender, String rawCommand) {
		rawCommand = rawCommand.trim();

		if (rawCommand.startsWith("$")) {
			rawCommand = rawCommand.substring(1);
		}

		String[] astring = rawCommand.split(" ");
		String s = astring[0];
		astring = dropFirstString(astring);
		ISlackCommand slackCmd = commandMap.get(s);

		if (slackCmd == null) {
			SlackSender.getInstance().send("Command not recognized: " + s, "Server");
			return false;
		}

		return tryExecute(sender, astring, slackCmd, rawCommand);
	}

	public Map<String, ISlackCommand> getCommands() {
		return commandMap;
	}

	public List<ISlackCommand> getPossibleCommands() {
		List<ISlackCommand> list = Lists.<ISlackCommand> newArrayList();

		for (ISlackCommand ISlackCommand : commandSet) {
			list.add(ISlackCommand);
		}

		return list;
	}

	/**
	 * adds the command and any aliases it has to the internal map of available
	 * commands
	 */
	public ISlackCommand registerCommand(ISlackCommand command) {
		commandMap.put(command.getCommandName(), command);
		commandSet.add(command);

		for (String s : command.getCommandAliases()) {
			ISlackCommand ISlackCommand = commandMap.get(s);

			if ((ISlackCommand == null) || !ISlackCommand.getCommandName().equals(s)) {
				commandMap.put(s, command);
			}
		}

		return command;
	}

	protected boolean tryExecute(String sender, String[] args, ISlackCommand command, String input) {
		try {
			command.processCommand(sender, args);
			return true;
		} catch (WrongUsageException wrongusageexception) {
			ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.generic.usage",
					new Object[] { new ChatComponentTranslation(wrongusageexception.getMessage(),
							wrongusageexception.getErrorObjects()) });
			chatcomponenttranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
			SlackSender.getInstance().send(chatcomponenttranslation2.getUnformattedText(), "Server");
		} catch (CommandException commandexception) {
			ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(
					commandexception.getMessage(), commandexception.getErrorObjects());
			chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
			SlackSender.getInstance().send(chatcomponenttranslation1.getUnformattedText(), "Server");
		}

		return false;
	}
}
