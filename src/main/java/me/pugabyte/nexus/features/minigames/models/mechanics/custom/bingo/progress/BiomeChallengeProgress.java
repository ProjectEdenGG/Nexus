package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BiomeChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import org.bukkit.block.Biome;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.an;

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
			.map(biome -> an(camelCase(biome)) + " biome")
			.collect(Collectors.joining(" or ")));
	}

}
