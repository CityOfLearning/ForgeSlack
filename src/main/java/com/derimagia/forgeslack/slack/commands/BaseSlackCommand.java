package com.derimagia.forgeslack.slack.commands;

import java.util.Collections;
import java.util.List;

public abstract class BaseSlackCommand implements ISlackCommand {
	@Override
	public int compareTo(ISlackCommand cmdToCompare) {
		return getCommandName().compareTo(cmdToCompare.getCommandName());
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.<String>emptyList();
	}
}