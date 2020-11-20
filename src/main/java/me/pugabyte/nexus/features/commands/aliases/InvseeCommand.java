package me.pugabyte.nexus.features.commands.aliases;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

public class InvseeCommand extends CustomCommand {

	public InvseeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(OfflinePlayer player) {
		runCommand("openinv " + player.getName());
	}
}
