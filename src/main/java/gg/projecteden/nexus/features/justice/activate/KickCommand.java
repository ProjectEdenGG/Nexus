package gg.projecteden.nexus.features.justice.activate;

import gg.projecteden.nexus.features.commands.staff.admin.RebootCommand;
import gg.projecteden.nexus.features.justice.misc._PunishmentCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.NonNull;

import java.util.List;

@Permission(Group.MODERATOR)
public class KickCommand extends _PunishmentCommand {

	public KickCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<players> <reason...>")
	@Description("Kick a player or players")
	void run(@ErasureType(Punishments.class) List<Punishments> players, String input) {
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
