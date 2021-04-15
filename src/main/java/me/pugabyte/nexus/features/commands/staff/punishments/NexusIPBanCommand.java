package me.pugabyte.nexus.features.commands.staff.punishments;

import lombok.NonNull;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.utils.Env;

@Environments(Env.DEV)
@Permission("group.moderator")
//@Aliases("banip")
public class NexusIPBanCommand extends _PunishmentCommand {

	public NexusIPBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <time/reason...>")
	void run(Punishments punishments, String input) {
		punish(punishments, input);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.IP_BAN;
	}

}
