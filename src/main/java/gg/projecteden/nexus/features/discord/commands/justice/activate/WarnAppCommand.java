package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;

@Command("Warn a player")
public class WarnAppCommand extends _PunishmentActivateAppCommand {

	public WarnAppCommand(AppCommandEvent event) {
		super(event);
	}

}
