package me.pugabyte.bncore.features.minigames.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("Arena")
public class Arena implements ConfigurationSerializable {
	@NonNull
	private int id;
	@NonNull
	private String name;
	@NonNull
	private String displayName;
	@NonNull
	private MechanicType mechanicType;
	@NonNull
	private List<Team> teams;
	@NonNull
	private Lobby lobby;
	private Location spectateLocation;
	private Location respawnLocation;
	private int respawnSeconds = 5;
	private int seconds = 300;
	private int minPlayers = 2;
	private int maxPlayers = 30;
	private int winningScore;
	private int minWinningScore;
	private int maxWinningScore;
	private int lives = 0;
	private Set<Material> blockList;
	@Accessors(fluent = true)
	private boolean isWhitelist = true;
	@Accessors(fluent = true)
	private boolean canJoinLate = false;
	@Accessors(fluent = true)
	private boolean hasScoreboard = true;

	public Mechanic getMechanic() {
		return getMechanicType().get();
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("id", getId());
			put("name", getName());
			put("displayName", getDisplayName());
			put("mechanicType", getMechanicType().name());
			put("teams", getTeams());
			put("lobby", getLobby());
			put("spectateLocation", getSpectateLocation());
			put("respawnLocation", getRespawnLocation());
			put("respawnSeconds", getRespawnSeconds());
			put("seconds", getSeconds());
			put("minPlayers", getMinPlayers());
			put("maxPlayers", getMaxPlayers());
			put("winningScore", getWinningScore());
			put("minWinningScore", getMinWinningScore());
			put("maxWinningScore", getMaxWinningScore());
			put("lives", getLives());
			put("blockList", serializeMaterialSet(getBlockList()));
			put("isWhitelist", isWhitelist());
			put("canJoinLate", canJoinLate());
			put("hasScoreboard", hasScoreboard());
		}};
	}

	public Arena(Map<String, Object> map) {
		this.id = (int) map.get("id");
		this.name = (String) map.get("name");
		this.displayName = (String) map.get("displayName");
		this.mechanicType = MechanicType.valueOf(((String) map.get("mechanicType")).toUpperCase());
		this.teams = (List<Team>) map.get("teams");
		this.lobby = (Lobby) map.get("lobby");
		this.spectateLocation = (Location) map.get("spectateLocation");
		this.respawnLocation = (Location) map.get("respawnLocation");
		this.respawnSeconds = (Integer) map.getOrDefault("respawnSeconds", respawnSeconds);
		this.seconds = (Integer) map.getOrDefault("seconds", seconds);
		this.minPlayers = (Integer) map.getOrDefault("minPlayers", minPlayers);
		this.maxPlayers = (Integer) map.getOrDefault("maxPlayers", maxPlayers);
		this.winningScore = (Integer) map.getOrDefault("winningScore", winningScore);
		this.minWinningScore = (Integer) map.getOrDefault("minWinningScore", minWinningScore);
		this.maxWinningScore = (Integer) map.getOrDefault("maxWinningScore", maxWinningScore);
		this.lives = (Integer) map.getOrDefault("lives", lives);
		this.blockList = deserializeMaterialSet((List<String>) map.getOrDefault("blockList", blockList));
		this.isWhitelist = (Boolean) map.getOrDefault("isWhitelist", isWhitelist);
		this.canJoinLate = (Boolean) map.getOrDefault("canJoinLate", canJoinLate);
		this.hasScoreboard = (Boolean) map.getOrDefault("hasScoreboard", hasScoreboard);
	}

	List<String> serializeMaterialSet(Set<Material> materials) {
		if (materials == null) return null;
		return new ArrayList<String>(){{ addAll(materials.stream().map(Material::name).collect(Collectors.toList())); }};
	}

	Set<Material> deserializeMaterialSet(List<String> materials) {
		if (materials == null) return null;
		return materials.stream().map(block -> Material.valueOf(block.toUpperCase())).collect(Collectors.toSet());
	}

	public boolean ownsRegion(String regionName, String type) {
		return regionName.toLowerCase().matches(("^" + getMechanic().getName() + "_" + getName() + "_" + type + "_[0-9]+$").toLowerCase());
	}

	public boolean canUseBlock(Material type) {
		if (blockList == null || blockList.size() == 0)
			if (isWhitelist)
				return false;
			else
				return true;

		if (isWhitelist)
			return blockList.contains(type);
		else
			return !blockList.contains(type);
	}

}
