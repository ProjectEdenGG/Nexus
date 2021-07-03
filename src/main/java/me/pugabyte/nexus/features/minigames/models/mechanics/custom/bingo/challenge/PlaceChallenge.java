package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IItemChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.PlaceChallengeProgress;
import me.pugabyte.nexus.utils.FuzzyItemStack;

import java.util.Set;

@Data
@AllArgsConstructor
@ProgressClass(PlaceChallengeProgress.class)
public class PlaceChallenge implements IItemChallenge {
	private Set<FuzzyItemStack> items;

	public PlaceChallenge(FuzzyItemStack... items) {
		this.items = Set.of(items);
	}

}
