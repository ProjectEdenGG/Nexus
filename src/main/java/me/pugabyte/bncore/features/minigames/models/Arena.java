package me.pugabyte.bncore.features.minigames.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
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
	private Location respawnLocation;
	private int seconds;
	private int minPlayers;
	private int maxPlayers;
	private int winningScore;
	private int minWinningScore;
	private int maxWinningScore;
	// TODO: private Set<Material> blockList;
	private Location spectatePosition;
	@Accessors(fluent = true)
	private boolean canJoinLate;

	public Mechanic getMechanic() {
		return getMechanicType().get();
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("id", getId());
		map.put("name", getName());
		map.put("displayName", getDisplayName());
		map.put("mechanicType", getMechanicType().name());
		map.put("seconds", getSeconds());
		map.put("minPlayers", getMinPlayers());
		map.put("maxPlayers", getMaxPlayers());
		map.put("winningScore", getWinningScore());
		map.put("minWinningScore", getMinWinningScore());
		map.put("maxWinningScore", getMaxWinningScore());
		map.put("canJoinLate", canJoinLate());
		map.put("respawnLocation", getRespawnLocation());
		map.put("lobby", getLobby());
		map.put("teams", getTeams());

		return map;
	}

	public static Arena deserialize(Map<String, Object> map) {
		return Arena.builder()
				.id((int) map.get("id"))
				.name((String) map.get("name"))
				.displayName((String) map.get("displayName"))
				.teams((List<Team>) map.get("teams"))
				.mechanicType(MechanicType.valueOf(((String) map.get("mechanicType")).toUpperCase()))
				.lobby((Lobby) map.get("lobby"))
				.respawnLocation((Location) map.get("respawnLocation"))
				.seconds((int) map.getOrDefault("seconds", 600))
				.minPlayers((int) map.getOrDefault("minPlayers", 2))
				.maxPlayers((int) map.getOrDefault("maxPlayers", 10))
				.winningScore((int) map.getOrDefault("winningScore", 10))
				.minWinningScore((int) map.getOrDefault("minWinningScore", 0))
				.maxWinningScore((int) map.getOrDefault("maxWinningScore", 0))
				.canJoinLate((boolean) map.getOrDefault("canJoinLate", false))
				.build();
	}

}
