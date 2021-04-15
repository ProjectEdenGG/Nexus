package me.pugabyte.nexus.features.commands.staff.punishments;

import lombok.NonNull;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.utils.Env;

@Environments(Env.DEV)
@Permission("group.moderator")
//@Aliases("banip")
public class NexusIPBanCommand extends CustomCommand {

	public NexusIPBanCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

	@Path("<player> <time/reason...>")
	void ban(Punishments punishments, String input) {
		punishments.add(Punishment.ofType(PunishmentType.IP_BAN)
				.uuid(punishments.getUuid())
				.punisher(uuid())
				.input(input));
	}

}
