package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

@Data
public class Flag {
	// Spawn data
	@Getter
	@Setter
	@NonNull
	private Location spawnLocation;
	@Getter
	@Setter
	@NonNull
	private MaterialData flagMaterialData;
	@Getter
	@Setter
	@NonNull
	private BlockState flagBlockState;
	@Getter
	@Setter
	@NonNull
	private String[] lines;

	// Carrier data
	@Getter
	@Setter
	private Minigamer carrier;

	// Dropped data
	@Getter
	@Setter
	private Location currentLocation;
	@Getter
	@Setter
	private BlockState blockBelowState;
	@Getter
	@Setter
	private int taskId = -1;


}
