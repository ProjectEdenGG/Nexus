package gg.projecteden.nexus.features.discord.commands.justice;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.justice.Justice;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.Name;

@RequiredRole("Staff")
@HandledBy(Bot.KODA)
public class HistoryDiscordCommand extends NexusAppCommand {

	public HistoryDiscordCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "View a player's history", literals = false)
	void run(@Desc("Player") Punishments player) {
		if (player.hasHistory())
			replyEphemeral("<" + Justice.URL + "/history/" + Name.of(player) + ">");
		else
			replyEphemeral("No history found for " + player.getNickname());
	}

}
