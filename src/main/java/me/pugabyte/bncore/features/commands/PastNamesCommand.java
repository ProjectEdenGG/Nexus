package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.NerdService;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class PastNamesCommand extends CustomCommand {

	public PastNamesCommand(CommandEvent event) {
		super(event);
	}

	@Path("<target>")
	void run(@Arg("self") OfflinePlayer target) {
		List<String> pastNames = new NerdService().getPastNames(target.getUniqueId());
		if (pastNames.size() <= 1)
			error("No known past names for " + target.getName());

		send(PREFIX + "&e" + target.getName() + " &3previous known names:");
		send("&e" + String.join(", ", pastNames));
	}

}
