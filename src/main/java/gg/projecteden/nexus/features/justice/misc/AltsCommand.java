package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.NonNull;

@Permission(Group.MODERATOR)
public class AltsCommand extends _JusticeCommand {

	public AltsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("<player>")
	@Description("View a list of players that have logged in the with same IP")
	void alts(@Arg("self") Punishments player) {
		player.sendAltsMessage(this::send, () -> error("No alts found for &e" + player.getNickname()));
	}

}
