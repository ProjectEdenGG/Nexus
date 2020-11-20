package me.pugabyte.nexus.features.commands.aliases;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class UnvanishCommand extends CustomCommand {

	public UnvanishCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("vanish off");
	}

	@Path("gameworld")
	void gameworld() {
		runCommand("vanish off");
		runCommand("ch join m");
	}

	@Path("creative")
	void creative() {
		runCommand("vanish off");
		runCommand("ch join c");
	}
}
