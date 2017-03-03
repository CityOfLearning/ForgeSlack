package com.derimagia.forgeslack.slack.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.derimagia.forgeslack.slack.SlackSender;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WorldPoint;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class UserStatus extends BaseSlackCommand {

	@Override
	public String getCommandName() {
		return "user";
	}

	@Override
	public String getCommandUsage() {
		return "user\t[user] <perms|groups|locate> takes a username arguement and reports them";
	}

	@Override
	public void processCommand(String username, String[] args) throws WrongUsageException {
		if (args.length > 0) {
			String user = args[0];
			args = SlackCommandRegistry.dropFirstString(args);

			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(user);

			if (player != null) {
				switch (args[0]) {
				case "perms": {
					WorldPoint point = new WorldPoint(player);
					List<String> permissions = new ArrayList<>();
					permissions.add("Current Player Permissions are:");
					List<Zone> zones = APIRegistry.perms.getServerZone().getZonesAt(point);
					Collections.reverse(zones);
					for (Zone subZone : zones) {
						if (!(subZone instanceof RootZone) && !(subZone instanceof ServerZone)) {
							permissions.add("*Zone #" + subZone.getId() + "* - " + subZone.toString());
							for (GroupEntry group : APIRegistry.perms
									.getPlayerGroups(UserIdent.get(player.getPersistentID()))) {
								if (subZone.getGroupPermissions(group.getGroup()) != null) {
									permissions.add("*Group* " + group.getGroup());
									for (Entry<String, String> perm : subZone.getGroupPermissions(group.getGroup())
											.entrySet()) {
										permissions.add("" + perm.getKey() + " = " + perm.getValue());
									}
								}
							}
						}
					}
					SlackSender.getInstance().sendServer(StringUtils.join(permissions, "\n"));
				}
					break;
				case "groups": {
					SlackSender.getInstance()
							.sendServer("User *" + player.getDisplayNameString() + "* Groups\n" + StringUtils.join(
									APIRegistry.perms.getPlayerGroups(UserIdent.get(player.getPersistentID())), "\n"));
				}
					break;
				case "locate": {
					WorldPoint point = new WorldPoint(player);
					SlackSender.getInstance()
							.sendServer(String.format("%s is at %d, %d, %d in dim %d with gamemode %s",
									player.getName(), point.getX(), point.getY(), point.getZ(), player.dimension,
									player.theItemInWorldManager.getGameType().getName()));

					String zoneMsg = "Player is in zones:";
					List<Zone> zones = APIRegistry.perms.getServerZone().getZonesAt(point);
					Collections.reverse(zones);
					for (Zone zone : zones) {
						zoneMsg += "\n" + zone.getName();
					}
					SlackSender.getInstance().sendServer(zoneMsg);
				}
					break;
				default:
					SlackSender.getInstance().sendServer("Not a recognized Command");
					break;
				}

			} else {
				SlackSender.getInstance().sendServer("Cannot find player");
			}
		} else {
			throw new WrongUsageException("", new Object[] {});
		}
	}
}
