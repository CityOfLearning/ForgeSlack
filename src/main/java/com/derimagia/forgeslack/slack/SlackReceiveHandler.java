package com.derimagia.forgeslack.slack;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.handler.ConfigurationHandler;

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

			String token = request.getParameter("token");

			// have to filter otherwise slackbot will spam itself
			if (text.charAt(0) == '$') {
				if (!token.isEmpty() && token.equals(ConfigurationHandler.slackToken)) {
					if (!ForgeSlack.slackCommands.executeCommand(username, text)) {
						SlackSender.getInstance().sendServer("Failed to Execute Command");
					}
				}
			}
		}
	}
}