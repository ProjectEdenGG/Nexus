package me.pugabyte.nexus.features.discord.commands.justice.activate;

import me.pugabyte.nexus.features.discord.commands.justice._PunishmentDiscordCommand;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.Punishments;

public abstract class _PunishmentActivateDiscordCommand extends _PunishmentDiscordCommand {

	protected void execute(DiscordUser author, String name, String reason, boolean now) {
		Punishments.of(name).add(Punishment.ofType(getType())
				.punisher(author.getUuid())
				.input(reason)
				.now(now));
	}

}
