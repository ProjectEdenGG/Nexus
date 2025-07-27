package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.DimensionChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.World.Environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class DimensionChallengeProgress implements IChallengeProgress<DimensionChallenge> {
	@NonNull
	private Minigamer minigamer;
	private final Set<Environment> dimensions = new HashSet<>();

	@Override
	public Set<String> getRemainingTasks(DimensionChallenge challenge) {
		final Environment required = challenge.getDimension();
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
