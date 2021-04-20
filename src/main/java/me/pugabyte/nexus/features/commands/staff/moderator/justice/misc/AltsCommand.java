package me.pugabyte.nexus.features.commands.staff.moderator.justice.misc;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.JsonBuilder;

public class AltsCommand extends _JusticeCommand {

	public AltsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void alts(@Arg("self") Punishments player) {
		JsonBuilder json = player.getAltsMessage();
		if (json == null)
			error("No alts found for &e" + player.getNickname());

		send(json);
	}

}
