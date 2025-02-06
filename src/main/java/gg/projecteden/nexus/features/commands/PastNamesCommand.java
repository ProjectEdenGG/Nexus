package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;

import java.util.Set;

public class PastNamesCommand extends CustomCommand {

	public PastNamesCommand(CommandEvent event) {
		super(event);
	}

	@Path("<target>")
	@Description("View known previous names of a player")
	void run(@Arg("self") Nerd nerd) {
		Set<String> pastNames = nerd.getPastNames();
		if (pastNames.isEmpty())
			error("No known past names for " + nerd.getName());

		send(PREFIX + "&e" + nerd.getName() + " &3previous known names:");
		send("&e" + String.join(", ", pastNames));
	}

	@Permission(Permission.Group.ADMIN)
	@Path("remove <target> <name>")
	void remove(Nerd nerd, String name) {
		Set<String> pastNames = nerd.getPastNames();
		if (pastNames.isEmpty())
			error("No known past names for " + nerd.getName());

		pastNames.remove(name);
		nerd.setPastNames(pastNames);
		new NerdService().save(nerd);
		send(PREFIX + "&e" + name + " &3removed");
	}

}
