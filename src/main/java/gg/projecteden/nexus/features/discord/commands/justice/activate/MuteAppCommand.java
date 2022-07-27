package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;

@Command("Mute a player")
public class MuteAppCommand extends _PunishmentActivateAppCommand {

	public MuteAppCommand(AppCommandEvent event) {
		super(event);
	}

}
