package gg.projecteden.nexus.features.discord.commands.justice;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.features.justice.Justice;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.Name;

@RequiredRole("Staff")
@Command("View a player's history")
public class HistoryAppCommand extends NexusAppCommand {

	public HistoryAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "View a player's history", literals = false)
	void run(@Desc("Player") Punishments player) {
		if (!player.hasHistory())
			error("No history found for " + player.getNickname());

		replyEphemeral("<" + Justice.URL + "/history/" + Name.of(player) + ">");
	}

}
