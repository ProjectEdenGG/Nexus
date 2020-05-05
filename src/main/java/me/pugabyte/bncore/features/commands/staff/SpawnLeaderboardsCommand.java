package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.features.listeners.Leaderboards.Leaderboard;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;

import java.util.List;

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

	@ConverterFor(Leaderboard.class)
	Leaderboard convertToLeaderboard(String value) {
		try {
			return Leaderboard.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("Leaderboard from " + value + " not found");
		}
	}

	@TabCompleterFor(Leaderboard.class)
	List<String> tabCompleteLeaderboard(String filter) {
		return tabCompleteEnum(Leaderboard.class, filter);
	}

}
