package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

import java.util.Set;

@Description("View known previous names of a player")
public class PastNamesCommand extends CustomCommand {

	public PastNamesCommand(CommandEvent event) {
		super(event);
	}

	@Path("<target>")
	void run(@Arg("self") Nerd nerd) {
		Set<String> pastNames = nerd.getPastNames();
		if (pastNames.isEmpty())
			error("No known past names for " + nerd.getName());

		send(PREFIX + "&e" + nerd.getName() + " &3previous known names:");
		send("&e" + String.join(", ", pastNames));
	}

}
