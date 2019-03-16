package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

@Data
public class Flag {
	// Spawn data
	@NonNull
	private Location spawnLocation;
	@NonNull
	private MaterialData flagMaterialData;
	@NonNull
	private BlockState flagBlockState;
	@NonNull
	private String[] lines;
	@NonNull
	private Team team;

	// Carrier data
	private Minigamer carrier;

	// Dropped data
	private Location currentLocation;
	private BlockState blockBelowState;
	private int taskId = -1;


}
