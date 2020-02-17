package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class AllRecipesCommand extends CustomCommand {

	public AllRecipesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runConsoleCommand("minecraft:recipe give " + player().getName() + " *");
	}

}
