package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public interface IEntityChallengeProgress extends IChallengeProgress {

	List<EntityType> getKills();

	default boolean isCompleted(IChallenge challenge) {
		return false; // getRemainingTasks(challenge).isEmpty();
	}

	default Map<EntityType, Integer> getRemainingTasks(IChallenge challenge) {
		return null;
	}

}
