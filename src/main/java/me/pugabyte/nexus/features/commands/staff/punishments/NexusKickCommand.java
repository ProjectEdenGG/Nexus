package me.pugabyte.nexus.features.commands.staff.punishments;

import lombok.NoArgsConstructor;
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
import org.bukkit.event.Listener;

@Environments(Env.DEV)
@NoArgsConstructor
@Permission("group.moderator")
public class NexusKickCommand extends CustomCommand implements Listener {

	public NexusKickCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

	@Path("<player> <reason...>")
	void ban(Punishments punishments, String input) {
		punishments.add(Punishment.ofType(PunishmentType.KICK)
				.uuid(punishments.getUuid())
				.punisher(uuid())
				.input(input));
	}

}
