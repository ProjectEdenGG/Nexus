package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class TMCommand extends CustomCommand {

	public TMCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runConsoleCommand("essentials:sudo " + player().getName() + " c:" + argsString() + " ™");
	}

}
