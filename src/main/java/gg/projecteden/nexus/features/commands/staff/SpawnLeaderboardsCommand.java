package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.listeners.Leaderboards.Leaderboard;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission("group.staff")
public class SpawnLeaderboardsCommand extends CustomCommand {

	public SpawnLeaderboardsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("update <leaderboard>")
	void update(Leaderboard leaderboard) {
		leaderboard.updateActual();
		send(PREFIX + "Updated");
	}

}
