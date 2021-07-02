package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.KillChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IEntityChallengeProgress;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class KillChallengeProgress implements IEntityChallengeProgress {
	private final List<EntityType> kills = new ArrayList<>();

	public boolean isCompleted(IChallenge challenge) {
		return getRemainingTasks(challenge).isEmpty();
	}

	public Set<String> getRemainingTasks(IChallenge challenge) {
		final KillChallenge killChallenge = (KillChallenge) challenge;
		final Set<EntityType> required = killChallenge.getTypes();

		int remaining = killChallenge.getAmount();
		for (EntityType kill : kills) {
			if (!required.contains(kill))
				continue;

			--remaining;

			if (remaining == 0)
				return Collections.emptySet();
		}

		final String entityTypes = required.stream()
			.map(StringUtils::camelCase)
			.collect(Collectors.joining(" or "));

		return Set.of("Kill " + remaining + " " + entityTypes);
	}

}
