package me.pugabyte.nexus.features.commands.staff.moderator.justice.misc;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;

@NoArgsConstructor
public abstract class _JusticeCommand extends CustomCommand {

	public _JusticeCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

}
