package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.DeathSwap;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@MatchDataFor(DeathSwap.class)
public class DeathSwapMatchData extends MatchData {
	public Map<Minigamer, Swap> swaps = new HashMap<>();

	public DeathSwapMatchData(Match match) {
		super(match);
	}

	@Data
	public static class Swap {
		private final Minigamer swapped;
		private final LocalDateTime timestamp = LocalDateTime.now();
		private final Location location;

		public Swap(Minigamer swapped) {
			this.swapped = swapped;
			this.location = swapped.getPlayer().getLocation().clone();
		}

		public void with(Minigamer minigamer) {
			DeathSwapMatchData matchData = minigamer.getMatch().getMatchData();
			matchData.getSwaps().put(minigamer, this);
			minigamer.teleport(location);
			minigamer.tell("You swapped with &e" + swapped.getName());
		}
	}

	public Minigamer getKiller(Minigamer minigamer) {
		if (!swaps.containsKey(minigamer)) return null;
		Swap swap = swaps.get(minigamer);
		if (LocalDateTime.now().minusSeconds(20).isBefore(swap.getTimestamp()))
			return swap.getSwapped();
		return null;
	}

}
