package gg.projecteden.nexus.features.discord.commands.justice.deactivate;

import gg.projecteden.nexus.features.discord.commands.justice._PunishmentDiscordCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.Punishments;

import java.util.Optional;

public abstract class _PunishmentDeactivateDiscordCommand extends _PunishmentDiscordCommand {

	@Override
	protected void execute(DiscordUser author, String name, String reason, boolean now) {
		Punishments player = Punishments.of(name);
		Optional<Punishment> punishment = player.getMostRecentActive(getType());
		if (!punishment.isPresent())
			throw new InvalidInputException(player.getNickname() + " is not " + getType().getPastTense());

		punishment.get().deactivate(author.getUuid());
	}

}
