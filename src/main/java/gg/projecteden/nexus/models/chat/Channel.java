package gg.projecteden.nexus.models.chat;

import net.md_5.bungee.api.ChatColor;

import java.util.Set;

public interface Channel {

	Set<Chatter> getRecipients(Chatter chatter);

	String getAssignMessage(Chatter chatter);

	default ChatColor getMessageColor() {
		return ChatColor.WHITE;
	}

}
