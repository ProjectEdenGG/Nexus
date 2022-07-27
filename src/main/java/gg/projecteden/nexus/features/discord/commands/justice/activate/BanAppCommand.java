package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;

@Command("Ban a player")
public class BanAppCommand extends _PunishmentActivateAppCommand {

	public BanAppCommand(AppCommandEvent event) {
		super(event);
	}

}
