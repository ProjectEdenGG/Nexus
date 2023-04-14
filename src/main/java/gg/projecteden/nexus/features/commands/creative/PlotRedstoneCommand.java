package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

@Permission(Group.MODERATOR)
public class PlotRedstoneCommand extends CustomCommand {

	public PlotRedstoneCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Toggle redstone on a creative plot")
	void run(boolean state) {
		if (state)
			runCommand("plots flag set redstone true");
		else
			runCommand("plots flag set redstone false");

	}

}
