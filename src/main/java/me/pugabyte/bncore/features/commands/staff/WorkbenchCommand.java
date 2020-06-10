package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class WorkbenchCommand extends CustomCommand {

	public WorkbenchCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	public void workbench() {
		player().openWorkbench(null, true);
	}
}
