package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.listeners.Podiums.Podium;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission(Group.SENIOR_STAFF)
public class PodiumsCommand extends CustomCommand {

	public PodiumsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("update <leaderboard>")
	void update(Podium podium) {
		podium.updateActual();
		send(PREFIX + "Updated");
	}

}
