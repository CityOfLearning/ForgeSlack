package com.derimagia.forgeslack.handler;

import com.derimagia.forgeslack.ForgeSlack;
import com.derimagia.forgeslack.slack.SlackSender;
import com.dyn.utils.CCOLPlayerInfo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * @author derimagia
 */
public class EventHandler {

	private static String getName(EntityPlayer player) {
		if (!ForgeSlack.playerInfo.containsKey(player.getDisplayName())) {
			return player.getDisplayName().getUnformattedText();
		} else {
			//minecraft name and student name
			return player.getDisplayName().getUnformattedText() + " - "
					+ ForgeSlack.playerInfo.get(player.getDisplayName().getUnformattedText()).getDisplayName();
		}
	}

	@SubscribeEvent
	public void commandUsed(CommandEvent event) {
		if (event.sender.getCommandSenderEntity() instanceof EntityPlayer) {
			String command = "_[command]_ /" + event.command.getCommandName();
			for (String params : event.parameters) {
				command += " " + params;
			}
			SlackSender.getInstance().send(command, event.sender.getName());
		}
	}

	@SubscribeEvent
	public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
		// @TODO: Localize this?
		SlackSender.getInstance().send("_[Joined the Game]_", getName(event.player));

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!ForgeSlack.playerInfo.containsKey(event.player.getDisplayName().getUnformattedText())) {
					ForgeSlack.playerInfo.put(event.player.getDisplayName().getUnformattedText(), new CCOLPlayerInfo(getName(event.player)));
				}
			}
		}).start();

	}

	@SubscribeEvent
	public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		// @TODO: Localize this?
		SlackSender.getInstance().send("_[Left the Game]_", getName(event.player));
		if (ForgeSlack.playerInfo.containsKey(event.player.getDisplayName().getUnformattedText())) {
			ForgeSlack.playerInfo.remove(event.player.getDisplayName().getUnformattedText());
		}
	}

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			SlackSender.getInstance().send(
					"_" + event.entityLiving.getCombatTracker().getDeathMessage().getUnformattedText() + "_",
					getName((EntityPlayer) event.entityLiving));
		}
	}

	@SubscribeEvent
	public void onPlayerRecieveAchievement(AchievementEvent event) {
		if (event.entityPlayer instanceof EntityPlayerMP) {
			if (((EntityPlayerMP) event.entityPlayer).getStatFile().hasAchievementUnlocked(event.achievement)) {
				return;
			}
			if (!((EntityPlayerMP) event.entityPlayer).getStatFile().canUnlockAchievement(event.achievement)) {
				return;
			}

			IChatComponent achievementComponent = event.achievement.getStatName();
			IChatComponent achievementText = new ChatComponentText("[").appendSibling(achievementComponent)
					.appendText("]");

			String playerName = getName(event.entityPlayer);
			SlackSender.getInstance().send(
					"_" + playerName + " has earned the achievement: " + achievementText.getUnformattedText() + "_",
					playerName);
		}
	}

	@SubscribeEvent
	public void serverChat(ServerChatEvent event) {
		if (!(event.player instanceof FakePlayer)) {
			SlackSender.getInstance().send(event.message, getName(event.player));
		}
	}

}