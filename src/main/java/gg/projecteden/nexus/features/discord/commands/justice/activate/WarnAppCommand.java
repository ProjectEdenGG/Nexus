package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;

@Command("Warn a player")
public class WarnAppCommand extends _PunishmentActivateAppCommand {

	public WarnAppCommand(AppCommandEvent event) {
		super(event);
	}

}
