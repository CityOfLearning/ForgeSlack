package com.derimagia.forgeslack.slack.commands;

import java.util.List;

import com.derimagia.forgeslack.slack.SlackSender;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class Locate extends BaseSlackCommand {

	@Override
	public List<String> getCommandAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "locate";
	}

	@Override
	public String getCommandUsage() {
		return "locate, takes a username arguement and reports their location";
	}

	@Override
	public void processCommand(String username, String[] args) {
		String user = args[0];

		EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(user);

		if (player != null) {
			WorldPoint point = new WorldPoint(player);
			SlackSender.getInstance()
					.send(String.format("%s is at %d, %d, %d in dim %d with gamemode %s", player.getName(),
							point.getX(), point.getY(), point.getZ(), player.dimension,
							player.theItemInWorldManager.getGameType().getName()), "Server");

			String zones = "Player is in zones:";
			for (Zone zone : APIRegistry.perms.getServerZone().getZonesAt(point)) {
				zones += "\n" + zone.getName();
			}
			SlackSender.getInstance().send(zones, "Server");
		} else {
			SlackSender.getInstance().send("Cannot find player", "Server");
		}
	}
}
