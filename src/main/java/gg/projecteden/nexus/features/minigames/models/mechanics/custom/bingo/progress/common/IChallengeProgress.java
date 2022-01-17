package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;

import java.util.Set;

public interface IChallengeProgress {

	Minigamer getMinigamer();

	default boolean isCompleted(Challenge challenge) {
		return getRemainingTasks(challenge).isEmpty();
	}

	Set<String> getRemainingTasks(Challenge challenge);

}
