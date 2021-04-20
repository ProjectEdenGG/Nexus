package me.pugabyte.nexus.features.commands.staff.moderator.justice.remove;

import lombok.NonNull;
import me.pugabyte.nexus.features.commands.staff.moderator.justice.misc._PunishmentCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;

import java.util.List;

@Permission("group.moderator")
@Aliases({"unaltsban", "unbanalt", "unbanalts", "unbanip", "unipban"})
public class UnAltBanCommand extends _PunishmentCommand {

	public UnAltBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(@Arg(type = Punishments.class) List<Punishments> players) {
		deactivate(players);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.ALT_BAN;
	}

}
