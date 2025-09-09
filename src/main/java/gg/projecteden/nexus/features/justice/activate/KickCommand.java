package gg.projecteden.nexus.features.justice.activate;

import gg.projecteden.nexus.features.commands.staff.operator.RebootCommand;
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
public class KickCommand extends _PunishmentCommand {

	public KickCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<players> <reason...>")
	@Description("Kick a player or players")
	void run(@Arg(type = Punishments.class) List<Punishments> players, String input) {
		if (input.equals("Server restarting.")) {
			for (Punishments player : players)
				if (player.isOnline())
					RebootCommand.kick(player.getOnlinePlayer());

			return;
		}

		punish(players, input);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.KICK;
	}

}
