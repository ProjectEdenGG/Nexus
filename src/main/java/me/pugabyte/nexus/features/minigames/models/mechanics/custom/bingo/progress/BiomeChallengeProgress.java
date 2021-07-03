package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BiomeChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import org.bukkit.block.Biome;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.an;

@Data
@NoArgsConstructor
public class BiomeChallengeProgress implements IChallengeProgress {
	private final Set<Biome> biomes = new HashSet<>();

	@Override
	public Set<String> getRemainingTasks(IChallenge challenge) {
		final Set<Biome> required = ((BiomeChallenge) challenge).getBiomeTag().getValues();
		for (Biome biome : biomes)
			if (required.contains(biome))
				return Collections.emptySet();

		return Set.of("Visit " + required.stream()
			.map(biome -> an(camelCase(biome)) + " biome")
			.collect(Collectors.joining(" or ")));
	}

}
