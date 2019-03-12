package me.pugabyte.bncore.features.minigames.models;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import org.bukkit.Location;

import java.util.List;

@Data
public class Arena {
	@NonNull
	private String name;
	@NonNull
	private List<Team> teams;
	@NonNull
	private Mechanic mechanic;
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
	private Location eliminationTeleportPosition;
	@Accessors(fluent = true)
	private boolean canJoinLate;

}
