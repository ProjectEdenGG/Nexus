package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.Bingo;
import me.pugabyte.nexus.features.minigames.mechanics.Bingo.Challenge;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.LocationUtils.getCenteredLocation;

@Data
@MatchDataFor(Bingo.class)
public class BingoMatchData extends MatchData {
	private static final int size = 5;
	private Map<UUID, Location> spawnpoints = new HashMap<>();
	private Challenge[][] challenges = new Challenge[size][size];

	public BingoMatchData(Match match) {
		super(match);

		determineChallenges();
	}

	private void determineChallenges() {
		Iterator<Challenge> iterator = Challenge.shuffle().iterator();

		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				challenges[i][j] = iterator.next();
	}

	public void spawnpoint(Minigamer minigamer, Location location) {
		location = getCenteredLocation(location.clone().add(0, 2, 0));
		minigamer.teleport(location, true);
		spawnpoints.put(minigamer.getUniqueId(), location);
	}

}
