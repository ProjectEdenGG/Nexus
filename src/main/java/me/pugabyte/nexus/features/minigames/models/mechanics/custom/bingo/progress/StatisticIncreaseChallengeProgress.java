package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.matchdata.BingoMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StatisticIncreaseChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;

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

		for (Challenge challenge : minigamer.getMatch().<BingoMatchData>getMatchData().getAllChallenges()) {
			if (!(challenge.getChallenge() instanceof StatisticIncreaseChallenge statChallenge))
				continue;

			startingValues.put(challenge, statChallenge.getValue(minigamer.getOnlinePlayer()));
		}
	}

	@Override
	public Set<String> getRemainingTasks(Challenge challenge) {
		StatisticIncreaseChallenge statChallenge = challenge.getChallenge();
		final int startingValue = startingValues.get(challenge);
		final int currentValue = statChallenge.getValue(minigamer.getOnlinePlayer());
		final int completed = currentValue - startingValue;

		if (completed >= statChallenge.getAmount())
			return Collections.emptySet();

		// TODO Figure out how to display
		return Set.of(statChallenge.getStatistic().getKey().getKey());
	}

}
