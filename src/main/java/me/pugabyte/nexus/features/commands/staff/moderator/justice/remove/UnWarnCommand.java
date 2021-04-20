package me.pugabyte.nexus.features.commands.staff.moderator.justice.remove;

import lombok.NonNull;
import me.pugabyte.nexus.features.commands.staff.moderator.justice.misc._PunishmentCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;

import java.util.List;
import java.util.Optional;

// TODO Maybe remove in place of a unified history clearing command or buttons in /history?

@Permission("group.moderator")
public class UnWarnCommand extends _PunishmentCommand {

	public UnWarnCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(@Arg(type = Punishments.class) List<Punishments> players) {
		for (Punishments player : players) {
			try {
				Optional<Punishment> lastWarn = player.getLastWarn();
				if (lastWarn.isPresent())
					player.remove(lastWarn.get());
				else
					error(player.getNickname() + " does not have any warnings");
			} catch (Exception ex) {
				event.handleException(ex);
			}
		}
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.WARN;
	}

}
