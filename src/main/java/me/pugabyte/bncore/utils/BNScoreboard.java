package me.pugabyte.bncore.utils;

import me.lucko.helper.Services;
import me.lucko.helper.scoreboard.PacketScoreboardProvider;
import me.lucko.helper.scoreboard.Scoreboard;
import me.lucko.helper.scoreboard.ScoreboardObjective;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.utils.Utils.colorize;
import static me.pugabyte.bncore.utils.Utils.left;

@SuppressWarnings("unused")
public class BNScoreboard {
	private static final ScoreboardManager manager = BNCore.getInstance().getServer().getScoreboardManager();
	private static final Scoreboard scoreboard = Services.load(PacketScoreboardProvider.class).getScoreboard();
	private ScoreboardObjective objective;
	private Map<String, Integer> lines = new HashMap<>();

	public BNScoreboard(String title) {
		this(title, title, Collections.emptyList());
	}

	public BNScoreboard(String title, Player player) {
		this(title, title, Collections.singletonList(player));
	}

	public BNScoreboard(String title, Collection<? extends Player> players) {
		this(title, title, players);
	}

	public BNScoreboard(String id, String title) {
		this(id, title, Collections.emptyList());
	}

	public BNScoreboard(String id, String title, Player player) {
		this(id, title, Collections.singletonList(player));
	}

	public BNScoreboard(String id, String title, Collection<? extends Player> players) {
//		scoreboard.removeObjective(id);
		objective = scoreboard.createObjective(left(id, 16), colorize(title), DisplaySlot.SIDEBAR, false);
		for (Player player : players)
			addPlayer(player);
	}

	public void delete() {
		removePlayers(Bukkit.getOnlinePlayers());
		scoreboard.removeObjective(objective.getId());
	}

	private void clear() {
		objective.clearScores();
	}

	public void addPlayer(Player player) {
		objective.subscribe(player);
	}

	public void addPlayers(Player... players) {
		addPlayers(Arrays.asList(players));
	}

	public void addPlayers(Collection<? extends Player> players) {
		for (Player player : players)
			addPlayer(player);
	}

	public void removePlayer(Player player) {
		objective.unsubscribe(player);
		objective.unsubscribe(player, true);
		player.setScoreboard(manager.getMainScoreboard());
//		try {
//			FeatherBoardAPI.initScoreboard(player);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}

	public void removePlayers(Player... players) {
		removePlayers(Arrays.asList(players));
	}

	public void removePlayers(Collection<? extends Player> players) {
		for (Player player : players)
			removePlayer(player);
	}

	public Map<String, Integer> getLines() {
		return objective.getScores();
	}

	public void setLine(String id, int score) {
		objective.setScore(id, score);
	}

	public void setLines(Map<String, Integer> lines) {
		clear();
		objective.applyScores(lines);
	}

	public void removeLine(String id) {
		objective.removeScore(id);
	}

	public void removeLines(String... lines) {
		removeLines(Arrays.asList(lines));
	}

	public void removeLines(List<String> lines) {
		lines.forEach(this::removeLine);
	}

}
