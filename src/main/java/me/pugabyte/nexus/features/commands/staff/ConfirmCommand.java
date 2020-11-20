package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.moderator")
public class ConfirmCommand extends CustomCommand {

	public ConfirmCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("pr pv apply");
	}

}
