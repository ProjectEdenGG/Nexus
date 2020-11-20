package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

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
