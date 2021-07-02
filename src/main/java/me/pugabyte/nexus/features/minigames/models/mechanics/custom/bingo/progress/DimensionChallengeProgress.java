package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.DimensionChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import org.bukkit.World.Environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class DimensionChallengeProgress implements IChallengeProgress {
	private final Set<Environment> dimensions = new HashSet<>();

	@Override
	public Set<String> getRemainingTasks(IChallenge challenge) {
		final Environment required = ((DimensionChallenge) challenge).getDimension();
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
