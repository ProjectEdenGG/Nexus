package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission("group.admin")
public class BroadcastCommand extends CustomCommand {

	public BroadcastCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<message...>")
	void run(String message) {
		Broadcast.all().message(message).send();
	}

}
