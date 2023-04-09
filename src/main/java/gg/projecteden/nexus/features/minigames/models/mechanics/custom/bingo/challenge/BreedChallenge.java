package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IEntityChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.BreedChallengeProgress;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.EntityType;

import java.util.Set;

@Data
@AllArgsConstructor
@ProgressClass(BreedChallengeProgress.class)
public class BreedChallenge implements IEntityChallenge {
	private Set<EntityType> types;
	private int amount;

	public BreedChallenge(EntityType type, int amount) {
		this.types = Set.of(type);
		this.amount = amount;
	}

}
