package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.DimensionChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.Challenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import org.bukkit.World.Environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class DimensionChallengeProgress implements IChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final Set<Environment> dimensions = new HashSet<>();

	@Override
	public Set<String> getRemainingTasks(Challenge challenge) {
		final Environment required = ((DimensionChallenge) challenge.getChallenge()).getDimension();
		if (dimensions.contains(required))
			return Collections.emptySet();

		if (required == Environment.NORMAL)
			return Set.of("Visit the Overworld");
		if (required == Environment.NETHER)
			return Set.of("Visit the Nether");
		if (required == Environment.THE_END)
			return Set.of("Visit the End");

		return Set.of("Visit a custom dimension");
	}

}
