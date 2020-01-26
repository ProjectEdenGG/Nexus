package me.pugabyte.bncore.features.minigames.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@SerializableAs("Team")
public class Team implements ConfigurationSerializable {
	@NonNull
	private String name = "Default";
	@NonNull
	private ChatColor color = ChatColor.WHITE;
	private String objective;
	private Loadout loadout = new Loadout();
	private List<Location> spawnpoints = new ArrayList<>();
	private int balancePercentage = -1;

	public Team() {
		this(new HashMap<>());
	}

	public Team(String name) {
		this(new HashMap<String, Object>() {{ put("name", name); }});
	}

	public Team(Map<String, Object> map) {
		this.name = (String) map.getOrDefault("name", name);
		this.color = ChatColor.valueOf(((String) map.getOrDefault("color", color.name())).toUpperCase());
		this.objective = (String) map.get("objective");
		this.loadout = (Loadout) map.getOrDefault("loadout", loadout);
		this.spawnpoints = (List<Location>) map.getOrDefault("spawnpoints", spawnpoints);
		this.balancePercentage = (Integer) map.getOrDefault("balancePercentage", balancePercentage);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("name", ChatColor.stripColor(getName()));
			put("color", getColor().name());
			put("objective", getObjective());
			put("loadout", getLoadout());
			put("spawnpoints", getSpawnpoints());
			put("balancePercentage", getBalancePercentage());
		}};
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
				if (locs.size() == 0)
					locs.addAll(new ArrayList<>(spawnpoints));
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
