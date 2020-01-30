package me.pugabyte.bncore.features.commands.creative;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class DLRequestCommand extends CustomCommand {

	public DLRequestCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (!player().getWorld().getName().equalsIgnoreCase("creative")) {
			send(PREFIX + "You must be in the creative world to run this command.");
			return;
		}
		runCommand("ticket Plot download request");

	}
}
