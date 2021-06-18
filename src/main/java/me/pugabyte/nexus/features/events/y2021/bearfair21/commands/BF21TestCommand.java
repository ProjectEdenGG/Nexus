package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class BF21TestCommand extends CustomCommand {

	public BF21TestCommand(CommandEvent event) {
		super(event);
	}

	@Path("beacon")
	void beacon(){

	}
}
