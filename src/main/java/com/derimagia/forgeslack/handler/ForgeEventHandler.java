package com.derimagia.forgeslack.handler;

import com.derimagia.forgeslack.slack.SlackSender;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author derimagia
 */
public class ForgeEventHandler {

	@SubscribeEvent
	public void commandUsed(CommandEvent event) {
		if (event.sender.getCommandSenderEntity() instanceof EntityPlayer) {
			SlackSender.getInstance().send(event.command.toString(), event.sender.getName());
		}
	}

	@SubscribeEvent
	public void serverChat(ServerChatEvent event) {
		SlackSender.getInstance().send("_"+event.message+"_", event.username);
	}

}