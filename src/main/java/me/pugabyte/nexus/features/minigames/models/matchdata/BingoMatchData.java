package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.Bingo;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.LocationUtils.getCenteredLocation;

@Data
@MatchDataFor(Bingo.class)
public class BingoMatchData extends MatchData {
	private Map<UUID, Location> spawnpoints = new HashMap<>();

	public BingoMatchData(Match match) {
		super(match);
	}

	public void spawnpoint(Minigamer minigamer, Location location) {
		location = getCenteredLocation(location.clone().add(0, 2, 0));
		minigamer.teleport(location, true);
		spawnpoints.put(minigamer.getUniqueId(), location);
	}

}
