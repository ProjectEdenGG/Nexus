package me.pugabyte.nexus.features.commands.aliases;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class CRepeatCommand extends CustomCommand {

	public CRepeatCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("cpersist");
	}
}
