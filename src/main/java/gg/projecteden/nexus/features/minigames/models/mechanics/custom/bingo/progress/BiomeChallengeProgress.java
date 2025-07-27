package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BiomeChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Biome;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Extensions.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.an;

@Data
@RequiredArgsConstructor
public class BiomeChallengeProgress implements IChallengeProgress<BiomeChallenge> {
	private static final int LIMIT = 5;

	@NonNull
	private Minigamer minigamer;
	private final Set<Biome> biomes = new HashSet<>();

	@SuppressWarnings({"removal", "UnstableApiUsage"})
	@Override
	public Set<String> getRemainingTasks(BiomeChallenge challenge) {
		final Set<Biome> required = challenge.getBiomeTag().getValues();
		for (Biome biome : biomes)
			if (required.contains(biome))
				return Collections.emptySet();

		String materials = required.stream()
			.limit(LIMIT)
			.map(biome -> an(camelCase(biome.name())) + " biome")
			.collect(Collectors.joining(" or "));

		if (required.size() > LIMIT)
			materials += " or etc.";

		return Set.of("Visit " + materials);
	}

}
