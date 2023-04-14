package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.DescriptionExtra;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

public class HopperCommand extends CustomCommand {

	public HopperCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Enable hoppers on your LWC protection")
	@DescriptionExtra("Use this on the container the inventory is pointing into, not the hopper itself. Disabled by default.")
	void run(@Optional("on") boolean enable) {
		runCommand("chopper " + (enable ? "on" : "off"));
	}
}
