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
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	private Location respawnLocation;
	private Location spectatePosition;
	private int seconds;
	private int minPlayers;
	private int maxPlayers;
	private int winningScore;
	private int minWinningScore;
	private int maxWinningScore;
	// TODO: private Set<Material> blockList;
	@Accessors(fluent = true)
	private boolean canJoinLate;
	@Accessors(fluent = true)
	private boolean hasScoreboard = true;

	public Mechanic getMechanic() {
		return getMechanicType().getMechanic();
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
			put("respawnLocation", getRespawnLocation());
			put("spectatePosition", getSpectatePosition());
			put("seconds", getSeconds());
			put("minPlayers", getMinPlayers());
			put("maxPlayers", getMaxPlayers());
			put("winningScore", getWinningScore());
			put("minWinningScore", getMinWinningScore());
			put("maxWinningScore", getMaxWinningScore());
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
		this.respawnLocation = (Location) map.get("respawnLocation");
		this.spectatePosition = (Location) map.get("spectatePosition");
		this.seconds = (Integer) map.get("seconds");
		this.minPlayers = (Integer) map.get("minPlayers");
		this.maxPlayers = (Integer) map.get("maxPlayers");
		this.winningScore = (Integer) map.get("winningScore");
		this.minWinningScore = (Integer) map.get("minWinningScore");
		this.maxWinningScore = (Integer) map.get("maxWinningScore");
		this.canJoinLate = (Boolean) map.get("canJoinLate");
		this.hasScoreboard = (Boolean) map.getOrDefault("hasScoreboard", true);
	}

}
