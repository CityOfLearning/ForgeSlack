package com.derimagia.forgeslack.slack;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.handler.ConfigurationHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.DimensionManager;

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
			if (text.charAt(0) == '/') {
				String token = request.getParameter("token");

				if (!token.isEmpty() && token.equals(ConfigurationHandler.slackToken)) {
					// we probably need to do something to configure slack so it
					// only runs commands that we are ok with
					MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), text);
				} else {
					ForgeSlack.log.error("Token on Slack Outgoing WebHook is invalid! Ignoring Request.");
				}
			} else if (text.charAt(0) == '@') {
				String token = request.getParameter("token");

				if (!token.isEmpty() && token.equals(ConfigurationHandler.slackToken)) {

					String user = text.substring(1, text.indexOf(' '));
					String whisper = text.substring(text.indexOf(' '));

					String message = String.format("/w %s " + ConfigurationHandler.playerSignature + " %s", user,
							WordUtils.capitalizeFully(username), whisper);
					message = ConfigurationHandler.formatColors(message);
					MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
							message);

				} else {
					ForgeSlack.log.error("Token on Slack Outgoing WebHook is invalid! Ignoring Request.");
				}
			} else if (text.charAt(0) == '$') {
				// this is a special command from slack
				String cmd = text.substring(1, text.indexOf(' '));

				if (cmd.toLowerCase().equals("players")) {
					SlackSender.getInstance().send(String.format("Server has %d users logged on",
							MinecraftServer.getServer().getCurrentPlayerCount()), "Server");
					String users = "";
					for (String user : MinecraftServer.getServer().getAllUsernames()) {
						users += user + ", ";
					}
					SlackSender.getInstance().send("Usernames: " + users, "Server");
				} else if (cmd.toLowerCase().equals("locate")) {
					String user = text.substring(text.indexOf(' ')+1);

					EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager()
							.getPlayerByUsername(user);

					if (player != null) {
						// WorldPoint point = new WorldPoint(player);
						BlockPos point = player.getPosition();
						SlackSender.getInstance()
								.send(String.format("%s is at %d, %d, %d in dim %d with gamemode %s", player.getName(),
										point.getX(), point.getY(), point.getZ(), player.dimension,
										player.theItemInWorldManager.getGameType().getName()), "Server");
					}
				} else if (cmd.toLowerCase().equals("stats")) {
					DecimalFormat timeFormatter = new DecimalFormat("########0.000");

					String statMsg = "";
					statMsg += "Memory usage:\n";
					statMsg += "Max: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MiB\n";
					statMsg += "Total: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MiB\n";
					statMsg += "Free: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MiB\n";
					long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					statMsg += "Used: " + (used / 1024 / 1024) + " MiB\n";
					statMsg += "Average tick time: "
							+ timeFormatter.format(mean(MinecraftServer.getServer().tickTimeArray) * 1.0E-6D) + " ms\n";

					SlackSender.getInstance().send(statMsg, "Server");

					for (Integer dimId : DimensionManager.getIDs()) {
						double worldTickTime = mean(MinecraftServer.getServer().worldTickTimes.get(dimId)) * 1.0E-6D;
						double worldTPS = Math.min(1000.0 / worldTickTime, 20);
						SlackSender.getInstance()
								.send(String.format("%s : Mean tick time: %d ms. Mean TPS: %d",
										String.format("Dim %d ", dimId), timeFormatter.format(worldTickTime),
										timeFormatter.format(worldTPS)), "Server");
					}
					double meanTickTime = mean(MinecraftServer.getServer().tickTimeArray) * 1.0E-6D;
					double meanTPS = Math.min(1000.0 / meanTickTime, 20);
					SlackSender.getInstance().send(String.format("%s : Mean tick time: %d ms. Mean TPS: %d", "Overall",
							timeFormatter.format(meanTickTime), timeFormatter.format(meanTPS)), "Server");
				}

			} else {
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

			}
		}
	}

	private static long mean(long[] values) {
		long sum = 0l;
		for (long v : values) {
			sum += v;
		}

		return sum / values.length;
	}
}