package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

public class SitCommand extends CustomCommand {

	public SitCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Sit on the block you're looking at")
	void run() {
		runCommand("gsit");
	}
}
