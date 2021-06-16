package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IEntityChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.KillChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import org.bukkit.entity.EntityType;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class KillChallenge implements IEntityChallenge {
	private Set<EntityType> types;
	private int amount;

	public KillChallenge(EntityType type, int amount) {
		this.types = Set.of(type);
		this.amount = amount;
	}

	@Override
	public Class<? extends IChallengeProgress> getProgressClass() {
		return KillChallengeProgress.class;
	}

}
