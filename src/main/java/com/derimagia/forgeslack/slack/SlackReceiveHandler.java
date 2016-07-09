package com.derimagia.forgeslack.slack;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.handler.ConfigurationHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

/**
 * @author derimagia
 */
public class SlackReceiveHandler extends AbstractHandler {

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (request.getMethod().equals("POST")) {
			String username = request.getParameter("user_name");
			// we need to remove the whitespace as slack will not let you send
			// messages beginning with a slash unless its preceeded with a space
			String text = request.getParameter("text").trim();
			if (text.charAt(0) != '/') {
				String message = String.format("%s " + ConfigurationHandler.playerSignature + " %s",
						ConfigurationHandler.slackSignature, WordUtils.capitalizeFully(username), text);
				message = ConfigurationHandler.formatColors(message);
				String token = request.getParameter("token");

				if (!token.isEmpty() && token.equals(ConfigurationHandler.slackToken)) {
					if (!username.isEmpty() && !(username.trim().equals("slackbot"))) {
						MinecraftServer.getServer().getConfigurationManager()
								.sendChatMsg(new ChatComponentText(message));
					}
				} else {
					ForgeSlack.log.error("Token on Slack Outgoing WebHook is invalid! Ignoring Request.");
				}
			} else {
				String token = request.getParameter("token");

				if (!token.isEmpty() && token.equals(ConfigurationHandler.slackToken)) {
					// we probably need to do something to configure slack so it
					// only runs commands that we are ok with
					MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), text);
				} else {
					ForgeSlack.log.error("Token on Slack Outgoing WebHook is invalid! Ignoring Request.");
				}
			}
		}
	}
}