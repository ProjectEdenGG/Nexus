package gg.projecteden.nexus.features.justice.deactivate;

import gg.projecteden.nexus.features.justice.misc._PunishmentCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.NonNull;

import java.util.List;

@Permission(Group.MODERATOR)
public class UnWatchlistCommand extends _PunishmentCommand {

	public UnWatchlistCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Description("Unwatchlist a player")
	void run(@Arg(type = Punishments.class) List<Punishments> players) {
		deactivate(players);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.WATCHLIST;
	}

}
