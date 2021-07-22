package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class DoNothingCommand extends CustomCommand {

	public DoNothingCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nothing() {
	}

}
