package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class DoNothingCommand extends CustomCommand {

	public DoNothingCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nothing() {
	}

}
