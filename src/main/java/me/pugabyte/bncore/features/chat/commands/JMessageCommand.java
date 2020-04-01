package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.PrivateChannel;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases({"msg", "whisper", "w", "tell", "dm"})
public class JMessageCommand extends CustomCommand {
	private Chatter chatter;

	public JMessageCommand(@NonNull CommandEvent event) {
		super(event);
		chatter = ChatManager.getChatter(player());

	}

	@Path("<player> [message...]")
	void message(Chatter to, String message) {
		PrivateChannel dm = new PrivateChannel(chatter, to);
		if (isNullOrEmpty(message))
			chatter.setActiveChannel(dm);
		else
			ChatManager.process(chatter, dm, message);
	}
}
