package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class CRepeatCommand extends CustomCommand {

	public CRepeatCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("cpersist");
	}
}
