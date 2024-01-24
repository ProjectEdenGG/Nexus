package gg.projecteden.nexus.features.discord.commands.justice.activate;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.exceptions.AppCommandException;
import gg.projecteden.nexus.features.discord.commands.justice._PunishmentAppCommand;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.Punishments;

public abstract class _PunishmentActivateAppCommand extends _PunishmentAppCommand {

	public _PunishmentActivateAppCommand(AppCommandEvent event) {
		super(event);
	}

	protected void execute(DiscordUser author, Punishments player, String reason, boolean now) {
		if (player.getRank().isStaff())
			if (!author.getRank().isAdmin())
				throw new AppCommandException("You cannot " + getType().name().toLowerCase() + " staff members");

		player.add(Punishment.ofType(getType())
				.punisher(author.getUuid())
				.input(reason)
				.now(now));
	}

}
