package com.derimagia.forgeslack.slack;

import org.eclipse.jetty.server.Server;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.handler.ConfigurationHandler;
import com.derimagia.forgeslack.slack.commands.Broadcast;
import com.derimagia.forgeslack.slack.commands.DoMcCommand;
import com.derimagia.forgeslack.slack.commands.Help;
import com.derimagia.forgeslack.slack.commands.Locate;
import com.derimagia.forgeslack.slack.commands.ServerUsers;
import com.derimagia.forgeslack.slack.commands.Status;
import com.derimagia.forgeslack.slack.commands.Whisper;

/**
 * @author derimagia
 */
public class SlackReceiveServer {
	private Server server = null;

	public SlackReceiveServer() {
		ForgeSlack.log.info("Starting ForgeSlack on Port: " + ConfigurationHandler.jettyServerPort);
		server = new Server(ConfigurationHandler.jettyServerPort);
		server.setHandler(new SlackReceiveHandler());

		try {
			server.start();

			ForgeSlack.slackCommands.registerCommand(new Broadcast());
			ForgeSlack.slackCommands.registerCommand(new DoMcCommand());
			ForgeSlack.slackCommands.registerCommand(new Locate());
			ForgeSlack.slackCommands.registerCommand(new ServerUsers());
			ForgeSlack.slackCommands.registerCommand(new Status());
			ForgeSlack.slackCommands.registerCommand(new Help());
			ForgeSlack.slackCommands.registerCommand(new Whisper());

		} catch (Exception e) {
			ForgeSlack.log.error("Error starting ForgeSlack server: " + e);
			e.printStackTrace();
		}
	}
}