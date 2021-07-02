package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.Challenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;

import java.util.Set;

public interface IChallengeProgress {

	default boolean isCompleted(Challenge challenge) {
		return isCompleted(challenge.getChallenge());
	}

	default boolean isCompleted(IChallenge challenge) {
		return getRemainingTasks(challenge).isEmpty();
	}

	default Set<String> getRemainingTasks(Challenge challenge) {
		return getRemainingTasks(challenge.getChallenge());
	}

	Set<String> getRemainingTasks(IChallenge challenge);

}
