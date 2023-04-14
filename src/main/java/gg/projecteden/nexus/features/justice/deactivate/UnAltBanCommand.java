package gg.projecteden.nexus.features.justice.deactivate;

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
public class UnAltBanCommand extends _PunishmentCommand {

	public UnAltBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<player>")
	@Description("Un-alt-ban a player")
	void run(@ErasureType(Punishments.class) List<Punishments> players) {
		deactivate(players);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.ALT_BAN;
	}

}
