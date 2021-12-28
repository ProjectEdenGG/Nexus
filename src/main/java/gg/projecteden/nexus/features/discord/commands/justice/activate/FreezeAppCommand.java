package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;

@Command("Freeze a player")
public class FreezeAppCommand extends _PunishmentActivateAppCommand {

	public FreezeAppCommand(AppCommandEvent event) {
		super(event);
	}

}
