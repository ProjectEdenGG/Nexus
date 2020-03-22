package me.pugabyte.bncore.features.chat.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class JoinCommand extends CustomCommand {

	public JoinCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("herochat join " + arg(1));
	}

}
