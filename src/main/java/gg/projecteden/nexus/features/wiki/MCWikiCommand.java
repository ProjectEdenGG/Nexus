package gg.projecteden.nexus.features.wiki;

import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Aliases("minecraftwiki")
public class MCWikiCommand extends _WikiSearchCommand {

	MCWikiCommand(CommandEvent event) {
		super(event);
	}

	@Override
	WikiType getWikiType() {
		return WikiType.MINECRAFT;
	}

}
