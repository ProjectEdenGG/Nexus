package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;

@Command("Kick a player")
public class KickAppCommand extends _PunishmentActivateAppCommand {

	public KickAppCommand(AppCommandEvent event) {
		super(event);
	}

}
