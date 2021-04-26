package me.pugabyte.nexus.features.commands.staff.moderator.justice.activate;

import lombok.NonNull;
import me.pugabyte.nexus.features.commands.staff.moderator.justice.misc._PunishmentCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;

import java.util.List;

@Permission("group.moderator")
public class KickCommand extends _PunishmentCommand {

	public KickCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> [reason...]")
	void run(@Arg(type = Punishments.class) List<Punishments> players, String input) {
		punish(players, input);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.KICK;
	}

}
