package gg.projecteden.nexus.features.discord.commands.justice.deactivate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;

@Command("Unfreeze a player")
public class UnFreezeAppCommand extends _PunishmentDeactivateAppCommand {

	public UnFreezeAppCommand(AppCommandEvent event) {
		super(event);
	}

}
