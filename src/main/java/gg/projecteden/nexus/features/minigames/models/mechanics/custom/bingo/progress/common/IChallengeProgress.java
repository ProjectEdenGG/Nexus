package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;

import java.util.Set;

public interface IChallengeProgress<T extends IChallenge> {

	Minigamer getMinigamer();

	default boolean isCompleted(T challenge) {
		return getRemainingTasks(challenge).isEmpty();
	}

	Set<String> getRemainingTasks(T challenge);

}
