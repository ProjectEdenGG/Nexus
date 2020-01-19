package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("cls")
public class ClearScreenCommand extends CustomCommand {

	public ClearScreenCommand(CommandEvent event) {
		super(event);
	}

	@Path("[lines]")
	void clearScreen(@Arg("20") Integer lines) {
		for (int i = 0; i < lines; i++) {
			reply("");
		}
	}
}
