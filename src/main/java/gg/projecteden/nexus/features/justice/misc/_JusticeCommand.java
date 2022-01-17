package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.features.justice.Justice;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public abstract class _JusticeCommand extends CustomCommand {

	public _JusticeCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Justice.PREFIX;
		DISCORD_PREFIX = Justice.DISCORD_PREFIX;
	}

}
