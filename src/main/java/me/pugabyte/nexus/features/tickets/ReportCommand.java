package me.pugabyte.nexus.features.tickets;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

public class ReportCommand extends CustomCommand {

	public ReportCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <reason...>")
	void report(OfflinePlayer player, String reason) {
		runCommand("ticket " + player.getName() + ": " + reason);
	}

}
