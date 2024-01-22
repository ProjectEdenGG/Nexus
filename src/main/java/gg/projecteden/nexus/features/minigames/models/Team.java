package gg.projecteden.nexus.features.minigames.models;

import com.destroystokyo.paper.Title;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNamed;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ActionBarUtils.ActionBar;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.LocationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scoreboard.NameTagVisibility;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gg.projecteden.nexus.features.minigames.menus.teams.TeamColorMenu.COLOR_TYPES;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Data
@AllArgsConstructor
@SerializableAs("Team")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Team implements ConfigurationSerializable, IsColoredAndNamed, Colored, IHasSpawnpoints {
	@NonNull
	@EqualsAndHashCode.Include
	private String name = "Default";
	@NonNull
	@EqualsAndHashCode.Include
	private ChatColor chatColor = ChatColor.WHITE;
	private String objective;
	private Loadout loadout = new Loadout();
	private List<Location> spawnpoints = new ArrayList<>();
	private int lives = 0;
	private int minPlayers = 1;
	private int maxPlayers = -1;
	private int balancePercentage = -1;
	private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;

	public Team() {
		this(new HashMap<>());
	}

	public Team(String name) {
		this(new HashMap<>() {{
			put("name", name);
		}});
	}

	public Team(Map<String, Object> map) {
		this.name = (String) map.getOrDefault("name", name);
		this.chatColor = ChatColor.valueOf(((String) map.getOrDefault("color", chatColor.name())).toUpperCase());
		this.objective = (String) map.get("objective");
		this.loadout = (Loadout) map.getOrDefault("loadout", loadout);
		this.spawnpoints = (List<Location>) map.getOrDefault("spawnpoints", spawnpoints);
		this.lives = (Integer) map.getOrDefault("lives", lives);
		this.minPlayers = (Integer) map.getOrDefault("minPlayers", minPlayers);
		this.maxPlayers = (Integer) map.getOrDefault("maxPlayers", maxPlayers);
		this.balancePercentage = (Integer) map.getOrDefault("balancePercentage", balancePercentage);
		this.nameTagVisibility = NameTagVisibility.valueOf((String) map.getOrDefault("nameTagVisibility", nameTagVisibility.name()));
	}

	@Override
	@NotNull
	public Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			put("name", stripColor(getName()));
			put("color", getChatColor().name());
			put("objective", getObjective());
			put("loadout", getLoadout());
			put("spawnpoints", getSpawnpoints());
			put("lives", getLives());
			put("minPlayers", getMinPlayers());
			put("maxPlayers", getMaxPlayers());
			put("balancePercentage", getBalancePercentage());
			put("nameTagVisibility", getNameTagVisibility().name());
		}};
	}

	public @NotNull Color getColor() {
		return chatColor.getColor();
	}

	public void spawn(Match match) {
		spawn(getMinigamers(match));
	}

	public void spawn(Minigamer minigamer) {
		spawn(Collections.singletonList(minigamer));
	}

	public void spawn(List<Minigamer> minigamers) {
		if (minigamers.isEmpty()) return;
		minigamers = new ArrayList<>(minigamers);

		minigamers.forEach(minigamer -> {
			minigamer.getOnlinePlayer().setGameMode(minigamer.getMatch().getMechanic().getGameMode());
			minigamer.getOnlinePlayer().getInventory().setHeldItemSlot(0);
		});

		if (loadout != null)
			minigamers.forEach(minigamer -> loadout.apply(minigamer));

		toSpawnpoints(minigamers);
	}

	public void toSpawnpoints(Match match) {
		toSpawnpoints(getAliveMinigamers(match));
	}

	public void toSpawnpoints(List<Minigamer> members) {
		Validate.notEmpty(members, "Members argument should not be empty");
		members = new ArrayList<>(members);
		Match match = members.get(0).getMatch();
		Validate.notNull(match, "Minigamers must be in a match");
		// convoluted sanity checking of inputs but honestly this is extra and just a waste of CPU
//		Match match = members.stream().filter(minigamer -> minigamer.getMatch() != null).findFirst().orElseThrow(() -> new IllegalArgumentException("Minigamers must be in a match")).getMatch();
//		assert match != null;
//		if (!members.stream().allMatch(minigamer -> match.equals(minigamer.getMatch())))
//			throw new IllegalArgumentException("All minigamers must be in the same match");

		Validate.notEmpty(spawnpoints, "Team " + getName() + " has no spawnpoints!");

		if (spawnpoints.size() == 1) {
			Location spawnpoint = spawnpoints.get(0);
			for (Minigamer minigamer : members)
				minigamer.teleportAsync(spawnpoint);
			return;
		}

		Set<Location> usedSpawnpoints = match.getUsedSpawnpoints();
		List<Location> locations = null;
		boolean shuffle = match.getMechanic().shuffleSpawnpoints();

		while (!members.isEmpty()) {
			if (locations == null || locations.isEmpty()) {
				locations = new ArrayList<>(spawnpoints);
				if (!usedSpawnpoints.isEmpty()) {
					// remove spawnpoints from the same block/location
					locations.removeIf(location1 -> usedSpawnpoints.stream().anyMatch(location2 -> LocationUtils.blockLocationsEqual(location1, location2)));

					if (locations.isEmpty()) {
						// we have run out of spawns, time to clear the used spawnpoints
						usedSpawnpoints.clear();
						locations = new ArrayList<>(spawnpoints);
					}
				}
				if (shuffle)
					Collections.shuffle(locations);
			}
			Location spawnpoint = locations.remove(0);
			members.remove(0).teleportAsync(spawnpoint);
			usedSpawnpoints.add(spawnpoint);
		}
	}

	public List<Minigamer> getAliveMinigamers(Match match) {
		return getAliveMinigamers(getMinigamers(match));
	}

	public List<Minigamer> getAliveMinigamers(List<Minigamer> minigamers) {
		return ensureThisTeam(minigamers.stream().filter(Minigamer::isAlive));
	}

	public List<Minigamer> getMinigamers(Match match) {
		return ensureThisTeam(match.getMinigamers());
	}

	/**
	 * Returns a list containing all minigamers from the input list that are on this team.
	 * @param minigamers input minigamers of varying teams
	 * @return new list of minigamers on this team
	 */
	public List<Minigamer> ensureThisTeam(List<Minigamer> minigamers) {
		return ensureThisTeam(minigamers.stream());
	}

	/**
	 * Returns a list containing all minigamers from the input stream that are on this team.
	 * @param minigamers input minigamers of varying teams
	 * @return new list of minigamers on this team
	 */
	public List<Minigamer> ensureThisTeam(Stream<Minigamer> minigamers) {
		return minigamers
				.filter(minigamer -> this.equals(minigamer.getTeam()))
				.collect(Collectors.toList());
	}

	public int getScore(Match match) {
		return match.getScores().getOrDefault(this, 0);
	}

	public ColorType getColorType() {
		ColorType colorType = COLOR_TYPES.stream()
			.filter(colorType1 -> getChatColor().equals(colorType1.getChatColor()))
			.findFirst()
			.orElse(null);

		if (colorType == null)
			Nexus.warn("Could not find a matching color type for team " + getName() + " (Color: " + getChatColor().getName() + ")");

		return colorType;
	}

	public void broadcast(Match match, String text) {
		getAliveMinigamers(match).forEach(minigamer -> minigamer.tell(text));
	}

	public void broadcastNoPrefix(Match match, String text) {
		getAliveMinigamers(match).forEach(minigamer -> minigamer.sendMessage(text));
	}

	public void title(Match match, Title title) {
		getAliveMinigamers(match).forEach(minigamer -> minigamer.getOnlinePlayer().sendTitle(title));
	}

	public void actionBar(Match match, ActionBar actionBar) {
		getAliveMinigamers(match).forEach(minigamer -> ActionBarUtils.sendActionBar(minigamer.getOnlinePlayer(), actionBar));
	}

}
