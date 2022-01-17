package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IItemChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.ObtainChallengeProgress;
import gg.projecteden.nexus.utils.FuzzyItemStack;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
@ProgressClass(ObtainChallengeProgress.class)
public class ObtainChallenge implements IItemChallenge {
	private Set<FuzzyItemStack> items;

	public ObtainChallenge(FuzzyItemStack... items) {
		this.items = Set.of(items);
	}

}
