package gg.projecteden.nexus.features.discord.commands.justice.deactivate;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;

@Command("Unban a player")
public class UnBanAppCommand extends _PunishmentDeactivateAppCommand {

	public UnBanAppCommand(AppCommandEvent event) {
		super(event);
	}

}
