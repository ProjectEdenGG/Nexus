package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;

@Command("Watchlist a player")
public class WatchlistAppCommand extends _PunishmentActivateAppCommand {

	public WatchlistAppCommand(AppCommandEvent event) {
		super(event);
	}

}
