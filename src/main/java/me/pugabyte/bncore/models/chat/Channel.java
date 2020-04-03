package me.pugabyte.bncore.models.chat;

import org.bukkit.ChatColor;

import java.util.Set;

public interface Channel {

	Set<Chatter> getRecipients(Chatter chatter);

	String getAssignMessage(Chatter chatter);

	default ChatColor getMessageColor() {
		return ChatColor.WHITE;
	}

}
