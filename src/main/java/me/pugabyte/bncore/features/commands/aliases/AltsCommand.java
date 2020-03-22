package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class AltsCommand extends CustomCommand {

	public AltsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("dupeip " + argsString());
	}

}
