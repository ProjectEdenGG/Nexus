package gg.projecteden.nexus.features.discord.commands.justice.deactivate;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;

@Command("Unaltban a player")
public class UnAltBanAppCommand extends _PunishmentDeactivateAppCommand {

	public UnAltBanAppCommand(AppCommandEvent event) {
		super(event);
	}

}
