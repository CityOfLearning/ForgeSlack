package com.derimagia.forgeslack.handler;

import com.derimagia.forgeslack.slack.SlackSender;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
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

	@SubscribeEvent
	public void commandUsed(CommandEvent event) {
		if (event.sender.getCommandSenderEntity() instanceof EntityPlayer) {
			System.out.println(event.command.getCommandName() + " " + event.parameters);
			SlackSender.getInstance().send(event.command.getCommandName() + " " + event.parameters,
					event.sender.getName());
		}
	}

	@SubscribeEvent
	public void serverChat(ServerChatEvent event) {
		SlackSender.getInstance().send("_" + event.message + "_", event.username);
	}
	
	private static String getName(EntityPlayer player) {
		return ScorePlayerTeam.formatPlayerName(player.getTeam(), player.getDisplayName().getUnformattedText());
	}

	@SubscribeEvent
	public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
		// @TODO: Localize this?
		SlackSender.getInstance().send("_[Joined the Game]_", getName(event.player));
	}

	@SubscribeEvent
	public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		// @TODO: Localize this?
		SlackSender.getInstance().send("_[Left the Game]_", getName(event.player));
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

}