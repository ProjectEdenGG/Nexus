package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.NerdService;
import org.bukkit.entity.Player;

import java.util.List;

public class PastNamesCommand extends CustomCommand {

	public PastNamesCommand(CommandEvent event) {
		super(event);
	}

	@Path("<target>")
	void run(@Arg("self") Player target) {
		List<String> pastNames = new NerdService().getPastNames(target.getUniqueId());
		String names = "";
		for (int i = 0; i < pastNames.size(); i++) {
			if (i == pastNames.size() - 1) names += pastNames.get(i);
			names += pastNames.get(i) + ", ";
		}
		send(PREFIX + "&e" + target.getName() + " &3 previous known names:");
		send("&e" + names);
	}

}
