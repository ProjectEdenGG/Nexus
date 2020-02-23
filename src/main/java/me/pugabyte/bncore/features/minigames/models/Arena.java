package me.pugabyte.bncore.features.minigames.models;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.bncore.utils.YamlSerializationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("Arena")
public class Arena implements ConfigurationSerializable {
	@NonNull
	private int id = ArenaManager.getNextId();
	@NonNull
	private String name;
	@NonNull
	private String displayName;
	@NonNull
	private MechanicType mechanicType = MechanicType.FREE_FOR_ALL;
	@NonNull
	private List<Team> teams = new ArrayList<Team>() {{ add(new Team()); }};
	@NonNull
	private Lobby lobby = new Lobby();
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
	private Set<Material> blockList = new HashSet<>();
	@Accessors(fluent = true)
	private boolean isWhitelist = true;
	@Accessors(fluent = true)
	private boolean canJoinLate = false;

	public Mechanic getMechanic() {
		return getMechanicType().get();
	}

	public Arena(@NonNull String name) {
		this(new HashMap<String, Object>() {{ put("name", name); }});
	}

	public Arena(Map<String, Object> map) {
		this.id = (int) map.getOrDefault("id", id);
		this.name = (String) map.get("name");
		this.displayName = (String) map.getOrDefault("displayName", name);
		this.mechanicType = MechanicType.valueOf(((String) map.getOrDefault("mechanicType", mechanicType.name())).toUpperCase());
		this.teams = (List<Team>) map.getOrDefault("teams", teams);
		this.lobby = (Lobby) map.getOrDefault("lobby", lobby);
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
		this.blockList = YamlSerializationUtils.deserializeMaterialSet((List<String>) map.getOrDefault("blockList", new ArrayList<>()));
		this.isWhitelist = (Boolean) map.getOrDefault("isWhitelist", isWhitelist);
		this.canJoinLate = (Boolean) map.getOrDefault("canJoinLate", canJoinLate);
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
			put("blockList", YamlSerializationUtils.serializeMaterialSet(getBlockList()));
			put("isWhitelist", isWhitelist());
			put("canJoinLate", canJoinLate());
		}};
	}

	public String getRegionBaseName() {
		return (getMechanic().getClass().getSimpleName() + "_" + getName()).toLowerCase();
	}

	public String getRegionTypeRegex(String type) {
		return "^" + getRegionBaseName() + "_" + type.toLowerCase() + "(|_[0-9]+)$";
	}

	public boolean ownsRegion(String regionName, String type) {
		return regionName.toLowerCase().matches(getRegionTypeRegex(type));
	}

	public Region getRegion(String type) {
		return Minigames.getWorldGuardUtils().getRegion(getRegionBaseName() + "_" + type);
	}

	public int getRegionTypeId(String type) {
		return getRegionTypeId(getProtectedRegion(type));
	}

	public int getRegionTypeId(ProtectedRegion region) {
		return Integer.parseInt(region.getId().split("_")[3]);
	}

	public Set<ProtectedRegion> getRegionsLike(String regex) {
		return Minigames.getWorldGuardUtils().getRegionsLike(getRegionBaseName() + "_" + regex);
	}

	public ProtectedRegion getProtectedRegion(String type) {
		return Minigames.getWorldGuardUtils().getProtectedRegion(getRegionBaseName() + "_" + type);
	}

	public boolean canUseBlock(Material type) {
		if (blockList == null || blockList.size() == 0)
			return true;

		if (isWhitelist)
			return blockList.contains(type);
		else
			return !blockList.contains(type);
	}

	public void write() {
		ArenaManager.write(this);
	}

	public void delete() {
		ArenaManager.delete(this);
	}

	public void teleport(Minigamer minigamer) {
		if (respawnLocation != null)
			minigamer.teleport(respawnLocation);
		else if (spectateLocation != null)
			minigamer.teleport(spectateLocation);
		else if (lobby != null && lobby.getLocation() != null && !lobby.getLocation().equals(Minigames.getGamelobby()))
			minigamer.teleport(lobby.getLocation());
		else if (teams != null && teams.size() > 0 && teams.get(0).getSpawnpoints() != null && teams.get(0).getSpawnpoints().size() > 0)
			minigamer.teleport(teams.get(0).getSpawnpoints().get(0));
		else
			minigamer.tell("No teleport location found");
	}

}
