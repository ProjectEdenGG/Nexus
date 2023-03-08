package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.MODERATOR)
public class PlotRedstoneCommand extends CustomCommand {

	public PlotRedstoneCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Toggle redstone on a creative plot")
	void run(boolean state) {
		if (state)
			runCommand("plots flag set redstone true");
		else
			runCommand("plots flag set redstone false");

	}

}
