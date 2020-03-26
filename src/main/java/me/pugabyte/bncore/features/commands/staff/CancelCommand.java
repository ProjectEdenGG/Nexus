package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class CancelCommand extends CustomCommand {

	public CancelCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("pr pv cancel");
	}

}
