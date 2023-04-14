package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

public class SuggestionCommand extends CustomCommand {

	public SuggestionCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@HideFromWiki
	void run() {
		send(json("&3Make &esuggestions &3on our &c/discord").command("/discord"));
	}
}
