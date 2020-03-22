package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class JReplyCommand extends CustomCommand {
	private Chatter chatter;

	public JReplyCommand(@NonNull CommandEvent event) {
		super(event);
		chatter = ChatManager.getChatter(player());
	}
}
