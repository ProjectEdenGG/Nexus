package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IItemChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.ObtainChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import me.pugabyte.nexus.utils.FuzzyItemStack;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class ObtainChallenge implements IItemChallenge {
	private Set<FuzzyItemStack> items;

	public ObtainChallenge(FuzzyItemStack... items) {
		this.items = Set.of(items);
	}

	@Override
	public Class<? extends IChallengeProgress> getProgressClass() {
		return ObtainChallengeProgress.class;
	}

}
