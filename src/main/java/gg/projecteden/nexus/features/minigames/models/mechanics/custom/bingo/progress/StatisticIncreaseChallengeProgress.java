package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StatisticIncreaseChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Statistic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class StatisticIncreaseChallengeProgress implements IChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final Map<Challenge, Integer> startingValues = new HashMap<>();

	public StatisticIncreaseChallengeProgress(@NonNull Minigamer minigamer) {
		this.minigamer = minigamer;

		for (Challenge challenge : Challenge.values()) {
			if (!(challenge.getChallenge() instanceof StatisticIncreaseChallenge statChallenge))
				continue;

			startingValues.put(challenge, statChallenge.getValue(minigamer.getOnlinePlayer()));
		}
	}

	@Override
	public Set<String> getRemainingTasks(Challenge challenge) {
		final StatisticIncreaseChallenge statChallenge = challenge.getChallenge();
		final Statistic statistic = statChallenge.getStatistic();
		final String key = statistic.getKey().getKey().toLowerCase();

		final int startingValue = startingValues.get(challenge);
		final int currentValue = statChallenge.getValue(minigamer.getOnlinePlayer());
		final int completed = currentValue - startingValue;
		final int remaining = statChallenge.getAmount() - completed;

		if (completed >= statChallenge.getAmount())
			return Collections.emptySet();

		if (key.contains("_one_cm"))
			return Set.of(gg.projecteden.nexus.utils.StringUtils.camelCase(key.replace("_one_cm", "")) + " " + StringUtils.getDf().format(remaining / 100d) + " meters");

		if (statistic == Statistic.BREAK_ITEM)
			return Set.of("Break " + remaining + " " + gg.projecteden.nexus.utils.StringUtils.camelCase(statChallenge.getMaterial()));

		return Set.of(key);
	}

}
