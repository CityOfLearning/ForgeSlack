package com.derimagia.forgeslack.slack;

import org.eclipse.jetty.server.Server;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.handler.ConfigurationHandler;

/**
 * @author derimagia
 */
public class SlackReceiveServer {
	private Server server = null;

	public SlackReceiveServer() {
		server = new Server(ConfigurationHandler.jettyServerPort);
		server.setHandler(new SlackReceiveHandler());

		try {
			server.start();
		} catch (Exception e) {
			ForgeSlack.log.error("Error starting ForgeSlack server: " + e);
		}
	}
}