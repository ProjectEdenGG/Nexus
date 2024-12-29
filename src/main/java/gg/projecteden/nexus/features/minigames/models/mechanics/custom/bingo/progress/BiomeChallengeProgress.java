package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BiomeChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Biome;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class BiomeChallengeProgress implements IChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final Set<Biome> biomes = new HashSet<>();

	@Override
	public Set<String> getRemainingTasks(Challenge challenge) {
		final Set<Biome> required = ((BiomeChallenge) challenge.getChallenge()).getBiomeTag().getValues();
		for (Biome biome : biomes)
			if (required.contains(biome))
				return Collections.emptySet();

		return Set.of("Visit " + required.stream()
			.map(biome -> StringUtils.an(camelCase(biome)) + " biome")
			.collect(Collectors.joining(" or ")));
	}

}
