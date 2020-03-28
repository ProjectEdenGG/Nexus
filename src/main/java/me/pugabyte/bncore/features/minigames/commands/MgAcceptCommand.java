package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class MgAcceptCommand extends CustomCommand {

	public MgAcceptCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void mgaccept() {
		String command = MgInviteCommand.command;
		if (command == null)
			error("There is no pending game invite");
		runCommand(command);
	}
}
