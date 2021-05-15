package me.pugabyte.nexus.features.wiki;

import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

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
