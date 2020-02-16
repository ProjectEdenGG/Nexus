package me.pugabyte.bncore.features.minigames.managers;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {
	private static List<Match> matches = new ArrayList<>();

	static {

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

	public static Match getActiveMatchFromLocation(Mechanic mechanic, Location location) {
		for (ProtectedRegion region : Minigames.getWorldGuardUtils().getRegionsAt(location)) {
			Arena arena = ArenaManager.getFromRegion(region.getId());
			if (arena == null) continue;

			Match match = MatchManager.get(arena);
			if (!match.isMechanic(mechanic)) continue;
			if (!match.isStarted()) continue;

			return match;
		}
		return null;
	}

	public static void janitor() {
		List<Match> toRemove = new ArrayList<>();
		matches.forEach(match -> {
			if (match.getMinigamers() == null || match.getMinigamers().size() == 0) {
				match.end();
				toRemove.add(match);
			}
		});

		matches.removeAll(toRemove);
	}

}
