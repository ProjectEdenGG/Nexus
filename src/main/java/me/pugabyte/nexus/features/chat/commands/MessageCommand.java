package me.pugabyte.nexus.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PrivateChannel;

@Aliases({"m", "msg", "w", "whisper", "t", "tell", "pm", "dm"})
public class MessageCommand extends CustomCommand {
	private final Chatter chatter;

	public MessageCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatService().get(player());
	}

	@Path("<player> [message...]")
	void message(Chatter to, String message) {
		if (chatter == to)
			error("You cannot message yourself");

		PrivateChannel dm = new PrivateChannel(chatter, to);
		if (isNullOrEmpty(message))
			chatter.setActiveChannel(dm);
		else
			chatter.say(dm, message);
	}
}
