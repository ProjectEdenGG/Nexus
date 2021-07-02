package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IEntityChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.KillChallengeProgress;
import org.bukkit.entity.EntityType;

import java.util.Set;

@Data
@AllArgsConstructor
@ProgressClass(KillChallengeProgress.class)
public class KillChallenge implements IEntityChallenge {
	private Set<EntityType> types;
	private int amount;

	public KillChallenge(EntityType type, int amount) {
		this.types = Set.of(type);
		this.amount = amount;
	}

}
