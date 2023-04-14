package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

@HideFromWiki
public class DoNothingCommand extends CustomCommand {

	public DoNothingCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	void nothing() {
	}

}
