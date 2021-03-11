package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Aliases("craft")
@Permission("workbench")
public class WorkbenchCommand extends CustomCommand {

	public WorkbenchCommand(CommandEvent event) {
		super(event);
	}

	@Path
	public void workbench() {
		player().openWorkbench(null, true);
	}
}
