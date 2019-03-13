package me.pugabyte.bncore.features.minigames.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Builder
@Data
public class Team {
	@NonNull
	private ChatColor color;
	@NonNull
	private String name;
	@NonNull
	private String objective;
	@NonNull
	private Loadout loadout;
	@NonNull
	private List<Location> spawnpoints;
	private int balancePercentage = -1;

	public void spawn(List<Minigamer> minigamers) {
		List<Minigamer> members = new ArrayList<>(minigamers);
		members = members.stream()
				.filter(minigamer -> minigamer.getTeam().equals(this))
				.collect(Collectors.toList());

		members.forEach(minigamer -> loadout.apply(minigamer));

		while (members.size() > 0) {
			List<Location> locs = new ArrayList<>(spawnpoints);
			List<Minigamer> toRemove = new ArrayList<>();
			Random rand = new Random();
			for (Minigamer minigamer : members) {
				int randomIndex = rand.nextInt(locs.size());
				minigamer.teleport(locs.get(randomIndex));
				locs.remove(randomIndex);
				toRemove.add(minigamer);
			}
			members.removeAll(toRemove);
		}
	}

	public int getScore(Match match) {
		return match.getScores().get(this);
	}

	public List<Minigamer> getMinigamers(Match match) {
		return match.getMinigamers().stream()
				.filter(minigamer -> minigamer.getTeam().equals(this))
				.collect(Collectors.toList());
	}

}
