package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class SitCommand extends CustomCommand {

	public SitCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("gsit");
	}
}
