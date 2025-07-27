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
public class StatisticIncreaseChallengeProgress implements IChallengeProgress<StatisticIncreaseChallenge> {
	@NonNull
	private Minigamer minigamer;
	private final Map<StatisticIncreaseChallenge, Integer> startingValues = new HashMap<>();

	public StatisticIncreaseChallengeProgress(@NonNull Minigamer minigamer) {
		this.minigamer = minigamer;

		for (Challenge challenge : Challenge.values()) {
			if (!(challenge.getChallenge() instanceof StatisticIncreaseChallenge statChallenge))
				continue;

			startingValues.put(statChallenge, statChallenge.getValue(minigamer.getOnlinePlayer()));
		}
	}

	@Override
	public Set<String> getRemainingTasks(StatisticIncreaseChallenge challenge) {
		final Statistic statistic = challenge.getStatistic();
		final String key = statistic.getKey().getKey().toLowerCase();

		final int startingValue = startingValues.get(challenge);
		final int currentValue = challenge.getValue(minigamer.getOnlinePlayer());
		final int completed = currentValue - startingValue;
		final int remaining = challenge.getAmount() - completed;

		if (completed >= challenge.getAmount())
			return Collections.emptySet();

		if (key.contains("_one_cm"))
			return Set.of(gg.projecteden.nexus.utils.StringUtils.camelCase(key.replace("_one_cm", "")) + " " + StringUtils.getDf().format(remaining / 100d) + " meters");

		if (statistic == Statistic.BREAK_ITEM)
			return Set.of("Break " + remaining + " " + gg.projecteden.nexus.utils.StringUtils.camelCase(challenge.getMaterial()));

		return Set.of(key);
	}

}
