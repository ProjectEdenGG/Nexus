package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class BroadcastCommand extends CustomCommand {

	public BroadcastCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<message...>")
	void run(String message) {
		Chat.broadcast(message);
	}

}
