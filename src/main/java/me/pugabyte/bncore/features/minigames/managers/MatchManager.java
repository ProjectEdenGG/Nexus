package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatchManager {
	private static List<Match> matches = new ArrayList<>();

	static {
		BNCore.repeat(100, 100, MatchManager::janitor);
	}

	public static Optional<Match> get(Arena arena) {
		return matches.stream()
				.filter(_match -> _match.getArena().equals(arena))
				.findFirst();
	}

	public static List<Match> getAll() {
		return matches;
	}

	public static void remove(Match match) {
		matches.remove(match);
	}

	public static void add(Match match) {
		matches.add(match);
	}

	public static void janitor() {
		matches.removeIf(match -> match.getMinigamers() == null || match.getMinigamers().size() == 0);
	}

}
