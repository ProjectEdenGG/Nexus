package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class CRepeatCommand extends CustomCommand {

	public CRepeatCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("cpersist");
	}
}
