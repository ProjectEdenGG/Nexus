package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class HopperCommand extends CustomCommand {

	public HopperCommand(CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void run(@Arg("on") boolean enable) {
		runCommand("chopper " + (enable ? "on" : "off"));
	}
}
