package gg.projecteden.nexus.features.discord.commands.justice.deactivate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.nexus.features.discord.commands.justice._PunishmentAppCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.Punishments;

import java.util.Optional;

public abstract class _PunishmentDeactivateAppCommand extends _PunishmentAppCommand {

	public _PunishmentDeactivateAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Override
	protected void execute(DiscordUser author, Punishments player, String reason, boolean now) {
		Optional<Punishment> punishment = player.getMostRecentActive(getType());
		if (punishment.isEmpty())
			throw new InvalidInputException(player.getNickname() + " is not " + getType().getPastTense());

		punishment.get().deactivate(author.getUuid());
	}

}
