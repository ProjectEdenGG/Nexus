package me.pugabyte.nexus.features.chat.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Aliases({"me", "eme", "describe", "edescribe", "eaction"})
public class ActionCommand extends CustomCommand {

	public ActionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send("&cTemporarily disabled");
	}

}
