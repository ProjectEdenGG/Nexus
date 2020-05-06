package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.features.listeners.Leaderboards.Leaderboard;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class SpawnLeaderboardsCommand extends CustomCommand {

	public SpawnLeaderboardsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("update <leaderboard>")
	void update(Leaderboard leaderboard) {
		leaderboard.update();
		send(PREFIX + "Updated");
	}

}
