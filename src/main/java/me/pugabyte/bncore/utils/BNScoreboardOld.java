package me.pugabyte.bncore.utils;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@SuppressWarnings("unused")
public class BNScoreboardOld {
	private ScoreboardManager manager = BNCore.getInstance().getServer().getScoreboardManager();
	private Scoreboard scoreboard;
	private Objective objective;
	private Map<String, Integer> lines = new HashMap<>();

	public BNScoreboardOld(String title) {
		this(title, Collections.emptyList());
	}

	public BNScoreboardOld(String title, Player player) {
		this(title, Collections.singletonList(player));
	}

	public BNScoreboardOld(String title, Player... players) {
		this(title, Arrays.asList(players));
	}

	public BNScoreboardOld(String title, List<Player> players) {
		scoreboard = manager.getNewScoreboard();
		objective = scoreboard.registerNewObjective(colorize(title), title);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		for (Player player : players)
			addPlayer(player);
	}

	public void delete() {
		objective.unregister();
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getScoreboard().equals(scoreboard))
				removePlayer(player);
	}

	private void clear() {
		new HashMap<>(lines).forEach((id, score) -> removeLine(id));
	}

	private void update() {
		lines.forEach((id, score) -> objective.getScore(colorize(id)).setScore(score));
	}

	public void addPlayer(Player player) {
		player.setScoreboard(scoreboard);
	}

	public void addPlayers(Player... players) {
		addPlayers(Arrays.asList(players));
	}

	public void addPlayers(List<Player> players) {
		for (Player player : players)
			addPlayer(player);
	}

	public void removePlayer(Player player) {
		if (player.getScoreboard().equals(scoreboard)) {
			player.setScoreboard(manager.getMainScoreboard());
			FeatherBoardAPI.initScoreboard(player);
		}
	}

	public void removePlayers(Player... players) {
		removePlayers(Arrays.asList(players));
	}

	public void removePlayers(List<Player> players) {
		for (Player player : players)
			removePlayer(player);
	}

	public Map<String, Integer> getLines() {
		return lines;
	}

	public void setLine(String id, int score) {
		lines.put(id, score);
		update();
	}

	public void setLines(Map<String, Integer> lines) {
		clear();
		this.lines = lines;
		update();
	}

	public void removeLine(String id) {
		lines.remove(id);
		scoreboard.resetScores(colorize(id));
	}

	public void removeLines(String... lines) {
		removeLines(Arrays.asList(lines));
	}

	public void removeLines(List<String> lines) {
		lines.forEach(this::removeLine);
	}

}
