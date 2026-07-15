package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.listeners.Podiums.Podium;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.NonNull;

@Permission(Group.SENIOR_STAFF)
public class PodiumsCommand extends CustomCommand {

	public PodiumsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("update <leaderboard>")
	@Description("Update a podium")
	void update(Podium podium) {
		podium.updateActual();
		send(PREFIX + "Updated");
	}

	@Path("withTies <podium>")
	@Description("Debug podium data including ties")
	void withTies(Podium podium) {
		send(PREFIX + camelCase(podium) + " data including ties:");
		var top = podium.getTopWithTiesLastMonth();
		top.forEach((score, uuids) -> {
			send(score + ":");
			uuids.forEach(uuid -> send(" - " + Nickname.of(uuid)));
		});
	}

}
