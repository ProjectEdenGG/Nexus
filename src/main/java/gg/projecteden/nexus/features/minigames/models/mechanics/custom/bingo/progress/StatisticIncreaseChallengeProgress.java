package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.matchdata.BingoMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StatisticIncreaseChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import gg.projecteden.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Statistic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.utils.StringUtils.camelCase;

@Data
public class StatisticIncreaseChallengeProgress implements IChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final Map<Challenge, Integer> startingValues = new HashMap<>();

	public StatisticIncreaseChallengeProgress(@NonNull Minigamer minigamer) {
		this.minigamer = minigamer;

		for (Challenge challenge : minigamer.getMatch().<BingoMatchData>getMatchData().getAllChallenges()) {
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
			return Set.of(camelCase(key.replace("_one_cm", "")) + " " + StringUtils.getDf().format(remaining / 100d) + " meters");

		if (statistic == Statistic.BREAK_ITEM)
			return Set.of("Break " + remaining + " " + camelCase(statChallenge.getMaterial()));

		return Set.of(key);
	}

}
