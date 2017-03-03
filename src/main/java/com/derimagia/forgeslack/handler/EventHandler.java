package com.derimagia.forgeslack.handler;

import com.derimagia.forgeslack.slack.SlackSender;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * @author derimagia
 */
public class EventHandler {

	@SubscribeEvent
	public void commandUsed(CommandEvent event) {
		if (event.sender.getCommandSenderEntity() instanceof EntityPlayer) {
			String command = "_[command]_ /" + event.command.getCommandName();
			for (String params : event.parameters) {
				command += " " + params;
			}
			SlackSender.getInstance().send(command, (EntityPlayer) event.sender);
		}
	}

	@SubscribeEvent
	public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
		SlackSender.getInstance().send("_[Joined the Game]_", event.player);
	}

	@SubscribeEvent
	public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		SlackSender.getInstance().send("_[Left the Game]_", event.player);
	}

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			SlackSender.getInstance().send(
					"_" + event.entityLiving.getCombatTracker().getDeathMessage().getUnformattedText() + "_",
					(EntityPlayer) event.entityLiving);
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

			IChatComponent achievementComponent = new ChatComponentTranslation(
					event.achievement.getStatName().getUnformattedText());
			IChatComponent achievementText = new ChatComponentText("").appendSibling(achievementComponent);

			SlackSender.getInstance().send("Earned the achievement: " + achievementText.getUnformattedText(),
					event.entityPlayer);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void serverChat(ServerChatEvent event) {
		if (!(event.player instanceof FakePlayer)) {
			SlackSender.getInstance().send(event.message, event.player);
		}
	}

}