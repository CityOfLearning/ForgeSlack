package com.derimagia.forgeslack.slack.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.slack.SlackSender;

public class Help extends BaseSlackCommand {

	@Override
	public String getCommandName() {
		return "help";
	}

	protected Map<String, ISlackCommand> getCommands() {
		return ForgeSlack.slackCommands.getCommands();
	}

	@Override
	public String getCommandUsage() {
		return "help\tget possible commands";
	}

	protected List<ISlackCommand> getSortedPossibleCommands() {
		List<ISlackCommand> list = ForgeSlack.slackCommands.getPossibleCommands();
		Collections.sort(list);
		return list;
	}

	@Override
	public void processCommand(String username, String[] args) {
		List<ISlackCommand> list = getSortedPossibleCommands();

		String msg = "Possible Slack Commands\n----------------------------";

		for (ISlackCommand cmd : list) {
			msg += "\n" + cmd.getCommandUsage();
		}

		SlackSender.getInstance().sendServer(msg);
	}

}
