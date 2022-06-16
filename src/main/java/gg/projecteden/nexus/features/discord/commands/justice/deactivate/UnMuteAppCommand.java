package gg.projecteden.nexus.features.discord.commands.justice.deactivate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;

@Command("Unmute a player")
public class UnMuteAppCommand extends _PunishmentDeactivateAppCommand {

	public UnMuteAppCommand(AppCommandEvent event) {
		super(event);
	}

}
