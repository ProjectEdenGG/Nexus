package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatchManager {
	private static List<Match> matches = new ArrayList<>();

	public static Match get(Arena arena) {
		Match match;
		Optional<Match> optionalMatch = matches.stream()
				.filter(_match -> _match.getArena().equals(arena))
				.findFirst();
		if (!optionalMatch.isPresent()) {
			match = new Match(arena);
			add(match);
		} else {
			match = optionalMatch.get();
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

}
