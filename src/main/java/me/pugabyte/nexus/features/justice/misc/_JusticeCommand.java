package me.pugabyte.nexus.features.justice.misc;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.justice.Justice;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@NoArgsConstructor
public abstract class _JusticeCommand extends CustomCommand {

	public _JusticeCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Justice.PREFIX;
		DISCORD_PREFIX = Justice.DISCORD_PREFIX;
	}

}
