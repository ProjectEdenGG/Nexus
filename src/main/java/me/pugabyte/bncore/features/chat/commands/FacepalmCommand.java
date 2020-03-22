package me.pugabyte.bncore.features.chat.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class FacepalmCommand extends CustomCommand {

	public FacepalmCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommandAsConsole("essentials:sudo " + player().getName() + " c:" + argsString() + " (ლ‸－)");
	}

}
