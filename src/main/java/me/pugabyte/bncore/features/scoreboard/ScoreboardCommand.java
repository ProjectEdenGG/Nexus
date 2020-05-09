package me.pugabyte.bncore.features.scoreboard;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardCommand extends CustomCommand {
	private static final Map<UUID, ScoreboardUser> scoreboardUsers = new HashMap<>();
	private final ScoreboardUser user;

	public ScoreboardCommand(@NonNull CommandEvent event) {
		super(event);
		if (!scoreboardUsers.containsKey(player().getUniqueId()))
			scoreboardUsers.put(player().getUniqueId(), new ScoreboardUser(player().getUniqueId()));

		user = scoreboardUsers.get(player().getUniqueId());
	}

	@Path("create <line...>")
	void create(@Arg(type = ScoreboardLine.class) List<ScoreboardLine> lines) {
		user.setLines(lines);
		user.render();
	}

	@Path("render")
	void render() {
		user.render();
	}

	@Path("delete")
	void delete() {
		user.getScoreboard().delete();
		scoreboardUsers.remove(player().getUniqueId());
	}

}
