package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

import java.util.Set;

public class PastNamesCommand extends CustomCommand {

	public PastNamesCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<target>")
	@Description("View known previous names of a player")
	void run(@Optional("self") Nerd nerd) {
		Set<String> pastNames = nerd.getPastNames();
		if (pastNames.isEmpty())
			error("No known past names for " + nerd.getName());

		send(PREFIX + "&e" + nerd.getName() + " &3previous known names:");
		send("&e" + String.join(", ", pastNames));
	}

}
