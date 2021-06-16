package me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import me.pugabyte.nexus.features.menus.sabotage.tasks.ReactorTask;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ReactorTaskPartData extends SabotageTaskPartData {
	public ReactorTaskPartData(TaskPart task) {
		super(task);
	}

	@Override
	public int getDuration() {
		return 60;
	}

	@Override
	public boolean hasRunnable() {
		return true;
	}

	@Override
	public void runnable(Match match) {
		List<Player> players = match.getAlivePlayers().stream().filter(ReactorTask::isOpen).collect(Collectors.toList());
		if (players.size() == 2) {
			players.forEach(Player::closeInventory);
			match.<SabotageMatchData>getMatchData().endSabotage();
		}
	}
}
