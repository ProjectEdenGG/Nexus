package me.pugabyte.bncore.features.commands.creative;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.moderator")
public class RedstoneOnCommand extends CustomCommand {

	public RedstoneOnCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void run() {
		runCommand("plot set redstone true");
	}

}
