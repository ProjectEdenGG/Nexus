package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;

public interface IChallenge {

	Class<? extends IChallengeProgress> getProgressClass();

}
