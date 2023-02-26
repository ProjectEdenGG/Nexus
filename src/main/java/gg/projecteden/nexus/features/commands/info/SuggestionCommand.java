package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Description("Gives information on how to make suggestions for the server.")
public class SuggestionCommand extends CustomCommand {

	public SuggestionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void suggestion() {
		send(json("&3Make &esuggestions &3on our &c/discord").command("/discord"));
	}
}
