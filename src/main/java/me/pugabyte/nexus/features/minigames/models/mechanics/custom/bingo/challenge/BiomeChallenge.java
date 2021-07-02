package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.BiomeChallengeProgress;
import org.bukkit.block.Biome;

import java.util.Set;

@Data
@AllArgsConstructor
@ProgressClass(BiomeChallengeProgress.class)
public class BiomeChallenge implements IChallenge {
	private Set<Biome> biomes;

}
