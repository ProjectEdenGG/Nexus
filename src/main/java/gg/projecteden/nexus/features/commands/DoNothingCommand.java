package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@HideFromWiki
public class DoNothingCommand extends CustomCommand {

	public DoNothingCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nothing() {
	}

}
