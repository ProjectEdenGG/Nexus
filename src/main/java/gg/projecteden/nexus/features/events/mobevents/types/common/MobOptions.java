package gg.projecteden.nexus.features.events.mobevents.types.common;

import gg.projecteden.nexus.models.difficulty.DifficultyUser.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.EntityType;

@Data
@AllArgsConstructor
public class MobOptions {
	EntityType entityType;
	double weight;
	int cap;
	int spawnRadius;
	Difficulty difficulty;

	public MobOptions(EntityType type, double weight, int cap, int spawnRadius) {
		this.entityType = type;
		this.weight = weight;
		this.cap = cap;
		this.spawnRadius = spawnRadius;
		this.difficulty = Difficulty.MEDIUM;
	}
}
