package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;

public interface IChallenge {

	default Class<? extends IChallengeProgress> getProgressClass() {
		final ProgressClass annotation = this.getClass().getAnnotation(ProgressClass.class);
		if (annotation == null)
			throw new InvalidInputException(this.getClass().getSimpleName() + " does not have an @ProgressClass annotation");

		return annotation.value();
	}

	// Breaking
	// Placing
	// Crafting
	// Enchanting
	// Brewing
	// Cooking
	// Obtaining
	// Killing
	// Eating
	// Biome
	// Distance
	// Breeding
	// Taming
	// Advancement

	// Villager trade
	// Piglin trade
	// Exp level
	// Spawning
	//

}
