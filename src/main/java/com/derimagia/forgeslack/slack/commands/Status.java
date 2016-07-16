package com.derimagia.forgeslack.slack.commands;

import java.text.DecimalFormat;
import java.util.List;

import com.derimagia.forgeslack.slack.SlackSender;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

public class Status extends BaseSlackCommand {

	private static long mean(long[] values) {
		long sum = 0l;
		for (long v : values) {
			sum += v;
		}

		return sum / values.length;
	}

	DecimalFormat timeFormatter = new DecimalFormat("########0.000");

	@Override
	public List<String> getCommandAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "status";
	}

	@Override
	public String getCommandUsage() {
		return "status, give a comprehensive report of the server status";
	}

	@Override
	public void processCommand(String username, String[] args) {

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

		System.out.println(DimensionManager.getIDs().length);

		for (Integer dimId : DimensionManager.getIDs()) {
			double worldTickTime = mean(MinecraftServer.getServer().worldTickTimes.get(dimId)) * 1.0E-6D;
			double worldTPS = Math.min(1000.0 / worldTickTime, 20);
			SlackSender.getInstance()
					.send(String.format("%s : Mean tick time: %d ms. Mean TPS: %d", String.format("Dim %d ", dimId),
							timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)), "Server");
		}
		double meanTickTime = mean(MinecraftServer.getServer().tickTimeArray) * 1.0E-6D;
		double meanTPS = Math.min(1000.0 / meanTickTime, 20);
		SlackSender.getInstance().send(String.format("%s : Mean tick time: %d ms. Mean TPS: %d", "Overall",
				timeFormatter.format(meanTickTime), timeFormatter.format(meanTPS)), "Server");
	}

}
