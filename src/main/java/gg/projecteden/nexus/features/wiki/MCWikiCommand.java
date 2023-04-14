package gg.projecteden.nexus.features.wiki;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

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
