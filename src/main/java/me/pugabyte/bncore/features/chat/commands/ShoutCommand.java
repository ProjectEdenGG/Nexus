package me.pugabyte.bncore.features.chat.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class ShoutCommand extends CustomCommand {

	public ShoutCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("ch qm g " + argsString());
	}
}
