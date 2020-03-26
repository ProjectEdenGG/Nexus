package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class DoNothingCommand extends CustomCommand {

	public DoNothingCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nothing() {
	}

}
