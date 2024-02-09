package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IEntityChallenge;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IEntityChallengeProgress extends IChallengeProgress {

	String getAction();

	List<EntityType> getProgress();

	default Set<String> getRemainingTasks(Challenge challenge) {
		final IEntityChallenge killChallenge = challenge.getChallenge();
		final Set<EntityType> required = killChallenge.getTypes();

		int remaining = (int) (killChallenge.getAmount() - getProgress().stream().filter(required::contains).count());

		if (remaining <= 0)
			return Collections.emptySet();

		final String entityTypes = required.stream()
			.map(StringUtils::camelCase)
			.collect(Collectors.joining(" or "));

		return Set.of(getAction() + " " + remaining + " " + entityTypes);
	}

}
