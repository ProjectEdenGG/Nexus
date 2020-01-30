package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class AllCheatsCommand extends CustomCommand {

	public AllCheatsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("vanish on");
		runCommand("fly on");
		runCommand("god on");
		send("&3Vanish, god, and fly turned off");
	}
}
