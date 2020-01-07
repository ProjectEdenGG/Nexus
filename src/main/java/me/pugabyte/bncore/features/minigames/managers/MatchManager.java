package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {
	private static List<Match> matches = new ArrayList<>();

	static {
		Utils.repeat(100, 40, MatchManager::janitor);
	}

	public static Match find(Arena arena) {
		for (Match match : matches)
			if (match.getArena().equals(arena))
				return match;
		return null;
	}

	public static Match get(Arena arena) {
		Match match = find(arena);
		if (match == null) {
			match = new Match(arena);
			add(match);
		}
		return match;
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
