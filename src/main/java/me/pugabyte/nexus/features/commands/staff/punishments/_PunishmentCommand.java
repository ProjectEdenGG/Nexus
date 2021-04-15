package me.pugabyte.nexus.features.commands.staff.punishments;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;

public abstract class _PunishmentCommand extends CustomCommand {

	public _PunishmentCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

	protected void punish(Punishments punishments, String input) {
		punishments.add(Punishment.ofType(getType())
				.uuid(punishments.getUuid())
				.punisher(uuid())
				.input(input));
	}

	abstract protected PunishmentType getType();

}
