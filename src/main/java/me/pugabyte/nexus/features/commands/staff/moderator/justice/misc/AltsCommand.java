package me.pugabyte.nexus.features.commands.staff.moderator.justice.misc;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;

public class AltsCommand extends _JusticeCommand {

	public AltsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void alts(@Arg("self") Punishments player) {
		player.sendAltsMessage(this::send, () -> error("No alts found for &e" + player.getNickname()));
	}

}
