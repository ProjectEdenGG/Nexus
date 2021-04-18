package me.pugabyte.nexus.features.commands.staff.moderator.punishments;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public abstract class _PunishmentCommand extends CustomCommand {

	public _PunishmentCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

	protected void punish(List<Punishments> players) {
		punish(players, null);
	}

	protected void punish(List<Punishments> players, String input) {
		for (Punishments punishments : players) {
			try {
				punishments.add(Punishment.ofType(getType())
						.punisher(uuid())
						.input(input));
			} catch (Exception ex) {
				event.handleException(ex);
			}
		}
	}

	protected void deactivate(List<Punishments> players) {
		for (Punishments player : players) {
			try {
				Optional<Punishment> punishment = player.getLastActive(getType());
				if (punishment.isPresent())
					punishment.get().deactivate(uuid());
				else
					error(player.getNickname() + " is not " + getType().getPastTense());
			} catch (Exception ex) {
				event.handleException(ex);
			}
		}
	}

	abstract protected PunishmentType getType();

}
