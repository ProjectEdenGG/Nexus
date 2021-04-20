package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Interactables;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
@Aliases("bf21")
public class BearFair21Command extends CustomCommand {

	public BearFair21Command(CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		runCommand("bearfair21warp");
	}

	@Path("strengthTest")
	@Permission("group.admin")
	void strengthTest() {
		commandBlock();
		Interactables.strengthTest();
	}
}
