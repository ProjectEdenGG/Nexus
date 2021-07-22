package gg.projecteden.nexus.features.justice.deactivate;

import gg.projecteden.nexus.features.justice.misc._PunishmentCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.NonNull;

import java.util.List;

@Permission("group.moderator")
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
