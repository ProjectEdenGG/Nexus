package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class InvseeCommand extends CustomCommand {

	public InvseeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("openinv " + argsString());
	}
}
