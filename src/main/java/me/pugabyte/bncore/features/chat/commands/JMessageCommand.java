package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.PublicChannel;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class JMessageCommand extends CustomCommand {
	private Chatter chatter;

	public JMessageCommand(@NonNull CommandEvent event) {
		super(event);
		chatter = ChatManager.getChatter(player());

	}

	@Path("<player> [message...]")
	void message(Chatter to, String message) {
		if (isNullOrEmpty(message)) {
			PublicChannel dm = PublicChannel.builder()
					.name("PM / " + to.getPlayer().getName())
					.isPrivate(true)
					.build();

			chatter.setActiveChannel(dm);
		}

	}
}
