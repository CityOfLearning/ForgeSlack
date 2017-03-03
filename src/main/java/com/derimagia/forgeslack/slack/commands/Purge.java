package com.derimagia.forgeslack.slack.commands;

import com.derimagia.forgeslack.slack.SlackSender;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

public class Purge extends BaseSlackCommand {

	@Override
	public String getCommandName() {
		return "purge";
	}

	@Override
	public String getCommandUsage() {
		return "purge\tPurge the world from memory, ejects all players from that world";
	}

	@Override
	public void processCommand(String username, String[] args) throws WrongUsageException {
		if (args.length > 0) {
			try {
				int id = Integer.parseInt(args[0]);

				WorldServer world = DimensionManager.getWorld(id);

				if (world != null) {
					if (world.getChunkProvider() instanceof ChunkProviderServer) {
						for (EntityPlayer player : world.playerEntities) {
							player.addChatMessage(
									new ChatComponentText("World is being purged, returning you to the hub"));
							MinecraftServer.getServer().getCommandManager().executeCommand(player, "/spawn");
						}
						((ChunkProviderServer) world.getChunkProvider()).unloadAllChunks();
						SlackSender.getInstance().sendServer("Purged world successfully");
					} else {
						SlackSender.getInstance().sendServer("Unable to purge chunks");
					}
				} else {
					SlackSender.getInstance().sendServer("World does not exist");
				}
			} catch (NumberFormatException nfe) {
				SlackSender.getInstance().sendServer("Error parsing int");
			}
		} else {
			throw new WrongUsageException("", new Object[] {});
		}
	}
}
