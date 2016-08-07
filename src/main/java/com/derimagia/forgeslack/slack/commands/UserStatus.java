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
//import com.forgeessentials.api.APIRegistry;
//import com.forgeessentials.api.permissions.Zone;
//import com.forgeessentials.commons.selections.WorldPoint;
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
		return "user\t[user] <perms|groups> takes a username arguement and reports them";
	}

	@Override
	public void processCommand(String username, String[] args) throws WrongUsageException {
		if(args.length > 0){
			String user = args[0];
			SlackCommandRegistry.dropFirstString(args);

			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(user);

			if (player != null) {
				switch (args[0]) {
				case "perms": {
					WorldPoint point = new WorldPoint(player);
					List<String> permissions = new ArrayList<String>();
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
										permissions.add("*" + perm.getKey() + "* = " + perm.getValue());
									}
								}
							}
						}
					}
					SlackSender.getInstance().send(StringUtils.join(permissions, "\n"), "Server");
				}
					break;
				case "group": {
					SlackSender
							.getInstance().send(
									"User *" + player.getDisplayNameString() + "* Groups\n"
											+ StringUtils.join(APIRegistry.perms
													.getPlayerGroups(UserIdent.get(player.getPersistentID())), "\n"),
									"Server");
				}
					break;
				default:
					SlackSender.getInstance().send("Not a recognized Command", "Server");
					break;
				}

			} else {
				SlackSender.getInstance().send("Cannot find player", "Server");
			}
		} else {
			throw new WrongUsageException("", new Object[] {});
		}
	}
}
