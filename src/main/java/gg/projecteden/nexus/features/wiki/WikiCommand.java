package gg.projecteden.nexus.features.wiki;

import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

public class WikiCommand extends _WikiSearchCommand {

	public WikiCommand(CommandEvent event) {
		super(event);
	}

	@Override
	WikiType getWikiType() {
		return WikiType.SERVER;
	}

}
