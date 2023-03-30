package gg.projecteden.nexus.utils;

import fr.mrmicky.fastboard.FastBoard;
import gg.projecteden.nexus.Nexus;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
@Getter
@Setter
public class EdenScoreboard {
	private final Map<UUID, FastBoard> boards = new HashMap<>();
	private String title;
	private Map<String, Integer> lines = new HashMap<>();

	private static final ScoreboardManager manager = Nexus.getInstance().getServer().getScoreboardManager();

	public EdenScoreboard(String title) {
		this.title = title;
	}

	public EdenScoreboard(String title, Player... players) {
		this(players);
		this.title = title;
	}

	public EdenScoreboard(String title, Collection<? extends Player> players) {
		this(players);
		this.title = title;
	}

	public EdenScoreboard(Player... players) {
		this(Arrays.asList(players));
	}

	public EdenScoreboard(Collection<? extends Player> players) {
		players.forEach(this::subscribe);
	}

	public void delete() {
		for (UUID uuid : new HashSet<>(boards.keySet()))
			unsubscribe(Bukkit.getPlayer(uuid));
	}

	public boolean isSubscribed(Player player) {
		return boards.containsKey(player.getUniqueId());
	}

	public void subscribe(Player... players) {
		subscribe(Arrays.asList(players));
	}

	public void subscribe(Collection<? extends Player> players) {
		for (Player player : players)
			boards.computeIfAbsent(player.getUniqueId(), $ -> new FastBoard(player));
	}

	public void unsubscribe(Player... players) {
		unsubscribe(Arrays.asList(players));
	}

	public void unsubscribe(Collection<? extends Player> players) {
		players.forEach(player -> {
			if (!isSubscribed(player))
				return;

			FastBoard board = this.boards.remove(player.getUniqueId());
			if (board != null)
				board.delete();

			player.setScoreboard(manager.getMainScoreboard());
		});
	}

	public void setLine(String id, int score) {
		lines.put(id, score);
		update();
	}

	public void setLines(Map<String, Integer> lines) {
		this.lines = lines;
		update();
	}

	public void setLines(List<String> lines) {
		final List<String> copy = new ArrayList<>(lines);
		Collections.reverse(copy);

		Map<String, Integer> linesMap = new HashMap<>();

		int index = 0;
		for (String line : lines)
			linesMap.put(line, index++);

		setLines(linesMap);
	}

	public void removeLine(String line) {
		lines.remove(line);
		update();
	}

	public void removeLines(String... lines) {
		removeLines(Arrays.asList(lines));
	}

	public void removeLines(List<String> lines) {
		lines.forEach(this::removeLine);
	}

	private void update() {
		for (FastBoard board : boards.values())
			board.updateLines(lines); // TODO FastBoard doesnt support custom scores
	}

}
