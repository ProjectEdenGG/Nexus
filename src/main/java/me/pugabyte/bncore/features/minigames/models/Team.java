package me.pugabyte.bncore.features.minigames.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Builder
@Data
@SerializableAs("Team")
public class Team implements ConfigurationSerializable {
	@NonNull
	private String name;
	@NonNull
	private ChatColor color;
	@NonNull
	private String objective;
	private Loadout loadout;
	@NonNull
	private List<Location> spawnpoints;
	private int balancePercentage = -1;

	public static Team deserialize(Map<String, Object> map) {
		return Team.builder()
				.name((String) map.get("name"))
				.color(ChatColor.valueOf(((String) map.getOrDefault("color", ChatColor.WHITE)).toUpperCase()))
				.objective((String) map.get("objective"))
				.loadout((Loadout) map.get("loadout"))
				.spawnpoints((List<Location>) map.get("spawnpoints"))
				.build();
	}

	public String getColoredName() {
		return color + name;
	}

	public void spawn(Minigamer minigamer) {
		spawn(Collections.singletonList(minigamer));
	}

	public void spawn(List<Minigamer> minigamers) {
		List<Minigamer> members = new ArrayList<>(minigamers);
		members = members.stream()
				.filter(minigamer -> minigamer.getTeam().equals(this))
				.collect(Collectors.toList());

		members.forEach(minigamer -> {
			minigamer.getPlayer().setGameMode(minigamer.getMatch().getArena().getMechanic().getGameMode());
			minigamer.getPlayer().getInventory().setHeldItemSlot(0);
		});

		if (loadout != null)
			members.forEach(minigamer -> loadout.apply(minigamer));

		if (spawnpoints.size() == 1) {
			for (Minigamer minigamer : members)
				minigamer.teleport(spawnpoints.get(0));
			return;
		}

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

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("name", ChatColor.stripColor(getName()));
		map.put("color", getColor().name());
		map.put("objective", getObjective());
		map.put("loadout", getLoadout());
		map.put("spawnpoints", getSpawnpoints());

		return map;
	}

}
