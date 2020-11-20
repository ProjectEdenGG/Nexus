package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class SuggestionCommand extends CustomCommand {

	public SuggestionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void suggestion() {
		send(json("&3Make &esuggestions &3on our &c/discord").command("/discord"));
	}
}
