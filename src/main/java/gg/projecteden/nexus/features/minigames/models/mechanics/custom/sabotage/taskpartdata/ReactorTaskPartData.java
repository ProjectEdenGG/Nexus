package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.menus.sabotage.tasks.ReactorTask;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;
import gg.projecteden.parchment.HasPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReactorTaskPartData extends SabotageTaskPartData {

	private static final int PLAYER_TARGET = 2;

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

	private List<Player> getPlayers(Match match) {
		// TODO this will erroneously count the reactor as fixed if 2 people are standing on the same reactor
		return match.getAlivePlayers().stream().filter(player -> SmartInvsPlugin.isOpen(ReactorTask.class, ((HasPlayer) player).getPlayer())).toList();
	}

	@Override
	public void runnable(Match match) {
		List<Player> players = getPlayers(match);
		if (players.size() == PLAYER_TARGET) {
			players.forEach(Player::closeInventory);
			match.<SabotageMatchData>getMatchData().endSabotage();
		}
	}

	@Override
	public @NotNull String getBossBarTitle(Match match, int elapsed) {
		return task.getName() + " in " + (getDuration() - elapsed) + "s (" + getPlayers(match).size() + "/" + PLAYER_TARGET + ")";
	}
}
