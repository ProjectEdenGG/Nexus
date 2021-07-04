package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;

import java.util.Set;

public interface IChallengeProgress {

	Minigamer getMinigamer();

	default boolean isCompleted(Challenge challenge) {
		return getRemainingTasks(challenge).isEmpty();
	}

	Set<String> getRemainingTasks(Challenge challenge);

}
