package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.NonNull;

@Permission(Group.MODERATOR)
public class AltsCommand extends _JusticeCommand {

	public AltsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@NoLiterals
	@Path("<player>")
	@Description("View a list of players that have logged in the with same IP")
	void alts(@Optional("self") Punishments player) {
		player.sendAltsMessage(this::send, () -> error("No alts found for &e" + player.getNickname()));
	}

}
