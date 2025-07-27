package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Material;

public interface IChallenge {

	default Class<? extends IChallengeProgress<?>> getProgressClass() {
		final ProgressClass annotation = this.getClass().getAnnotation(ProgressClass.class);
		if (annotation == null)
			throw new InvalidInputException(this.getClass().getSimpleName() + " does not have an @ProgressClass annotation");

		return annotation.value();
	}

	Material getDisplayMaterial();

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
