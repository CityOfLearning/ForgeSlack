package com.derimagia.forgeslack.slack;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.handler.ConfigurationHandler;
import com.dyn.DYNServerMod;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import net.minecraft.entity.player.EntityPlayer;

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
		if (ConfigurationHandler.enabled) {
			api = new SlackApi(ConfigurationHandler.slackIncomingWebHook);
		}
	}

	/**
	 * Sends a Slack Message in a new Thread
	 *
	 * @param message
	 * @param username
	 */
	public void send(String message, EntityPlayer player) {
		if (ConfigurationHandler.enabled) {
			SlackMessage slackMessage = new SlackMessage();
			slackMessage.setUsername(ForgeSlack.getName(player));
			if (DYNServerMod.playersCcolInfo.containsKey(player)) {
				slackMessage.setText("*" + DYNServerMod.playersCcolInfo.get(player).getDisplayName() + "*-" + message);
			} else {
				slackMessage.setText(message);
			}

			// I think the 2d looks better
			slackMessage.setIcon("https://mcapi.ca/avatar/2d/" + ForgeSlack.getName(player));

			// Send in a new thread so it doesn't block the game.
			Thread thread = new Thread(new SlackSendThread(slackMessage));
			thread.start();
		}
	}

	/**
	 * Sends a Slack Message in a new Thread
	 *
	 * @param message
	 * @param username
	 */
	public void sendServer(String message) {
		if (ConfigurationHandler.enabled) {
			SlackMessage slackMessage = new SlackMessage();
			slackMessage.setUsername("Server");
			slackMessage.setText(message);
			// use the default icon
			slackMessage.setIcon("http://broad-participation.s3.amazonaws.com/logo.png");
			// Send in a new thread so it doesn't block the game.
			Thread thread = new Thread(new SlackSendThread(slackMessage));
			thread.start();
		}
	}
}