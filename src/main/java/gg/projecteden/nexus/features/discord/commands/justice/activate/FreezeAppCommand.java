package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;

@Command("Freeze a player")
public class FreezeAppCommand extends _PunishmentActivateAppCommand {

	public FreezeAppCommand(AppCommandEvent event) {
		super(event);
	}

}
