package gg.projecteden.nexus.features.discord.commands.justice.deactivate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;

@Command("Unban a player")
public class UnBanAppCommand extends _PunishmentDeactivateAppCommand {

	public UnBanAppCommand(AppCommandEvent event) {
		super(event);
	}

}
