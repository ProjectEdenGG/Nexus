package me.pugabyte.nexus.features.wiki;

import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class WikiCommand extends _WikiSearchCommand {

	public WikiCommand(CommandEvent event) {
		super(event);
	}

	@Override
	WikiType getWikiType() {
		return WikiType.SERVER;
	}

}
