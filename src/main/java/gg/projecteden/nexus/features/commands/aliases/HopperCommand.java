package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Description("Enable hoppers on your protection. Use this on the container the inventory is pointing into, not the hopper itself. Disabled by default.")
public class HopperCommand extends CustomCommand {

	public HopperCommand(CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void run(@Arg("on") boolean enable) {
		runCommand("chopper " + (enable ? "on" : "off"));
	}
}
