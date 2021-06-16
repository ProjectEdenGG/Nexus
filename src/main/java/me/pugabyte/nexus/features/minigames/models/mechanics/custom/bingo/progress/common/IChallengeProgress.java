package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.Challenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;

public interface IChallengeProgress {

	default boolean isCompleted(Challenge challenge) {
		return isCompleted(challenge.getChallenge());
	}

	boolean isCompleted(IChallenge challenge);

}
