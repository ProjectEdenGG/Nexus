package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;

@Command("Altban a player")
public class AltBanAppCommand extends _PunishmentActivateAppCommand {

	public AltBanAppCommand(AppCommandEvent event) {
		super(event);
	}

}
