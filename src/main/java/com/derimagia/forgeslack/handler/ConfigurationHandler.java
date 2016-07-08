package com.derimagia.forgeslack.handler;

import java.io.File;

import com.derimagia.forgeslack.ForgeSlack;

import net.minecraftforge.common.config.Configuration;

/**
 * @author derimagia
 */
public class ConfigurationHandler {
	public static Configuration configuration;
	public static boolean enabled = true;
	public static String slackIncomingWebHook = "";
	public static String slackToken = "";
	public static int jettyServerPort = 8085;
	public static String slackSignature = "";
	public static String playerSignature = "";
	
	public static final char COLOR_FORMAT_CHARACTER = '\u00a7';

	public static void init(File configFile) {
		if (configuration == null) {
			configuration = new Configuration(configFile);
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {
		enabled = configuration.getBoolean("enabled", Configuration.CATEGORY_GENERAL, true,
				"Whether ForgeSlack is enabled.");
		slackIncomingWebHook = configuration.getString("slackIncomingWebHook", Configuration.CATEGORY_GENERAL, "",
				"Slack Incoming WebHook URL");
		slackToken = configuration.getString("slackToken", Configuration.CATEGORY_GENERAL, "",
				"Token Slack provides to Accept Slack Messages");
		jettyServerPort = configuration.getInt("port", Configuration.CATEGORY_GENERAL, 8085, 1, 65535,
				"Port for Web Server to process Slack Messages");
		slackSignature = configuration.getString("slackSignature", Configuration.CATEGORY_GENERAL, "[&6Slack&r]",
				"How Slack Messages are formatted ");
		playerSignature = configuration.getString("playerSignature", Configuration.CATEGORY_GENERAL, "<%s>",
				"How Slack Messages usernames are formatted ");

		if (slackIncomingWebHook.isEmpty()) {
			enabled = false;
			ForgeSlack.log.error("Slack Incoming WebHook is empty. Disabling Mod.");
		}

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}
	
	/**
	 * Format color codes
	 *
	 * @param message
	 * @return formatted message
	 */
	public static String formatColors(String message) {
		// TODO: Improve this to replace codes less aggressively
		char[] b = message.toCharArray();
		for (int i = 0; i < (b.length - 1); i++) {
			if ((b[i] == '&') && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1)) {
				b[i] = COLOR_FORMAT_CHARACTER;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}
}