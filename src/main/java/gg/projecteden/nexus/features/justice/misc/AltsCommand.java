package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.NonNull;

@Permission("group.moderator")
public class AltsCommand extends _JusticeCommand {

	public AltsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("<player>")
	void alts(@Arg("self") Punishments player) {
		player.sendAltsMessage(this::send, () -> error("No alts found for &e" + player.getNickname()));
	}

}
