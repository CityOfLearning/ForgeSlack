package com.derimagia.forgeslack.slack;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.handler.ConfigurationHandler;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

/**
 * @author derimagia
 */
public class SlackSender {
	private static SlackSender instance;

	/**
	 * Returns the Instance
	 *
	 * @return SlackSender
	 */
	public static SlackSender getInstance() {
		if (instance == null) {
			instance = new SlackSender();
		}

		return instance;
	}

	public SlackApi api;

	public SlackSender() {
		api = new SlackApi(ConfigurationHandler.slackIncomingWebHook);
	}

	/**
	 * Sends a Slack Message in a new Thread
	 *
	 * @param message
	 * @param username
	 */
	public void send(String message, String username) {
		SlackMessage slackMessage = new SlackMessage();
		slackMessage.setText(message);
		if (ForgeSlack.playerInfo.containsKey(username)) {
			slackMessage.setUsername(ForgeSlack.playerInfo.get(username).getDisplayName() + "-" + username);
		} else {
			slackMessage.setUsername(username);
		}

		if (!username.toLowerCase().equals("server")) {
			// I think the 2d looks better
			slackMessage.setIcon("https://mcapi.ca/avatar/2d/" + username);
		} else {
			// use the default icon
			slackMessage.setIcon("https://dl.dropboxusercontent.com/u/33377940/logo.png");
		}

		// Send in a new thread so it doesn't block the game.
		Thread thread = new Thread(new SlackSendThread(slackMessage));
		thread.start();
	}
}