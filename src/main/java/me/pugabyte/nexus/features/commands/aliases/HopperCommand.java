package me.pugabyte.nexus.features.commands.aliases;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class HopperCommand extends CustomCommand {

	public HopperCommand(CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void run(@Arg("on") boolean enable) {
		runCommand("chopper " + (enable ? "on" : "off"));
	}
}
