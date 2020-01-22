package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class SuggestionCommand extends CustomCommand {

	public SuggestionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void suggestion() {
		send("&3Make &esuggestions &3on our &c/discord");
	}
}
