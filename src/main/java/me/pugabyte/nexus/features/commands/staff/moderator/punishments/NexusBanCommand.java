package me.pugabyte.nexus.features.commands.staff.moderator.punishments;

import lombok.NonNull;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.utils.Env;

import java.util.List;

@Environments(Env.DEV)
@Permission("group.moderator")
public class NexusBanCommand extends _PunishmentCommand {

	public NexusBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<players> <time/reason...>")
	void run(@Arg(type = Punishments.class) List<Punishments> players, String input) {
		punish(players, input);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.BAN;
	}

}
