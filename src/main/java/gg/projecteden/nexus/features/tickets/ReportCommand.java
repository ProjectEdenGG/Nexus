package gg.projecteden.nexus.features.tickets;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

public class ReportCommand extends CustomCommand {

	public ReportCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<player> <reason...>")
	@Description("Report a player to the staff team")
	void report(OfflinePlayer player, String reason) {
		runCommand("ticket " + Nickname.of(player) + ": " + reason);
	}

}
