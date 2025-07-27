package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IEntityChallenge;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IEntityChallengeProgress extends IChallengeProgress<IEntityChallenge> {
	int LIMIT = 5;

	String getAction();

	List<EntityType> getProgress();

	default Set<String> getRemainingTasks(IEntityChallenge challenge) {
		final Set<EntityType> required = challenge.getTypes();

		int remaining = (int) (challenge.getAmount() - getProgress().stream().filter(required::contains).count());

		if (remaining <= 0)
			return Collections.emptySet();

		String entityTypes = required.stream()
			.limit(LIMIT)
			.map(StringUtils::camelCase)
			.collect(Collectors.joining(" or "));

		if (required.size() > LIMIT)
			entityTypes += " or etc.";

		return Set.of(getAction() + " " + remaining + " " + entityTypes);
	}

}
