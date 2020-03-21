package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class BearFairCommand extends CustomCommand {

	public BearFairCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void bearfair() {
		send(PREFIX + "Coming soon!");
	}

}
