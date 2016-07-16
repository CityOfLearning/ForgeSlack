package com.derimagia.forgeslack.slack.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.derimagia.forgeslack.slack.SlackSender;

import net.minecraft.command.WrongUsageException;
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
	public String getCommandName() {
		return "status";
	}

	@Override
	public String getCommandUsage() {
		return "status, give a comprehensive report of the server status";
	}

	@Override
	public void processCommand(String username, String[] args) throws WrongUsageException {

		int dimCheck = -2;
		if (args.length > 0) {
			try {
				dimCheck = Integer.parseInt(args[0]);
			} catch (NumberFormatException nfe) {
				throw new WrongUsageException("Could not parse the dimension id, %s", new Object[] {getCommandUsage()});
			}
		}
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

		List<Integer> dims = new ArrayList<Integer>();

		for (Integer id : DimensionManager.getIDs()) {
			dims.add(id);
		}

		Collections.sort(dims);

		for (Integer dimId : dims) {

			double worldTickTime = mean(MinecraftServer.getServer().worldTickTimes.get(dimId)) * 1.0E-6D;
			double worldTPS = Math.min(1000.0 / worldTickTime, 20);
			if (dimCheck > -2) {
				if (dimId == dimCheck) {
					SlackSender.getInstance().send(String.format("Dim %d : Mean tick time: %s ms. Mean TPS: %s", dimId,
							timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)), "Server");
				}
			} else {
				SlackSender.getInstance().send(String.format("Dim %d : Mean tick time: %s ms. Mean TPS: %s", dimId,
						timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)), "Server");
			}
		}
		double meanTickTime = mean(MinecraftServer.getServer().tickTimeArray) * 1.0E-6D;
		double meanTPS = Math.min(1000.0 / meanTickTime, 20);
		SlackSender.getInstance().send(String.format("Overall: Mean tick time: %s ms. Mean TPS: %s",
				timeFormatter.format(meanTickTime), timeFormatter.format(meanTPS)), "Server");
	}

}
