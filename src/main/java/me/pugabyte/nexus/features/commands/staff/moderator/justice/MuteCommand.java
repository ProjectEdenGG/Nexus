package me.pugabyte.nexus.features.commands.staff.moderator.justice;

import lombok.NonNull;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.Env;

import java.util.List;

@Environments(Env.DEV)
@Permission("group.moderator")
@Aliases({"shutup", "stfu"})
public class MuteCommand extends _PunishmentCommand {

	public MuteCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> [time/reason...]")
	void run(@Arg(type = Punishments.class) List<Punishments> players, String input) {
		punish(players, input);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.MUTE;
	}

}
