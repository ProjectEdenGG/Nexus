package gg.projecteden.nexus.features.tickets;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

public class ReportCommand extends CustomCommand {

	public ReportCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <reason...>")
	void report(OfflinePlayer player, String reason) {
		runCommand("ticket " + Nickname.of(player) + ": " + reason);
	}

}
