package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CustomChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class CustomChallengeProgress implements IChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final Map<Challenge, Set<String>> progressMap = new HashMap<>();

	public Set<String> getProgress(Challenge challenge) {
		return progressMap.computeIfAbsent(challenge, $ -> new LinkedHashSet<>());
	}

	public void complete(Challenge challenge, String task) {
		getProgress(challenge).add(task);
	}

	@Override
	public Set<String> getRemainingTasks(Challenge challenge) {
		final Set<String> tasks = ((CustomChallenge) challenge.getChallenge()).getTasks();
		final Set<String> progress = getProgress(challenge);

		final LinkedHashSet<String> remaining = new LinkedHashSet<>(tasks);
		remaining.removeAll(progress);
		return remaining;
	}

}
