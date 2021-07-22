package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldGroup;

import static gg.projecteden.nexus.features.commands.WorkbenchCommand.PERMISSION;

@Aliases("craft")
@Permission(PERMISSION)
public class WorkbenchCommand extends CustomCommand {
	public static final String PERMISSION = "workbench";

	public WorkbenchCommand(CommandEvent event) {
		super(event);
	}

	@Path
	public void workbench() {
		if (worldGroup() == WorldGroup.EVENTS)
			permissionError();

		player().openWorkbench(null, true);
	}
}
