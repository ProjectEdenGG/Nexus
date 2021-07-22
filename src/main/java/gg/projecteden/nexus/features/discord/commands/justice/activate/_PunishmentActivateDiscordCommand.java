package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.nexus.features.discord.commands.justice._PunishmentDiscordCommand;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.Punishments;

public abstract class _PunishmentActivateDiscordCommand extends _PunishmentDiscordCommand {

	protected void execute(DiscordUser author, String name, String reason, boolean now) {
		Punishments.of(name).add(Punishment.ofType(getType())
				.punisher(author.getUuid())
				.input(reason)
				.now(now));
	}

}
