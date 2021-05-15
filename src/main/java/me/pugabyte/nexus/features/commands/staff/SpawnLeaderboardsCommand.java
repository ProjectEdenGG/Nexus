package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.features.listeners.Leaderboards.Leaderboard;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

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
