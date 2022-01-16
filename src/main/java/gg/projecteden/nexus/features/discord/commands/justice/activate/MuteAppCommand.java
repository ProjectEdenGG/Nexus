package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;

@Command("Mute a player")
public class MuteAppCommand extends _PunishmentActivateAppCommand {

	public MuteAppCommand(AppCommandEvent event) {
		super(event);
	}

}
