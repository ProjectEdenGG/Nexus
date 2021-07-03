package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.BiomeChallengeProgress;
import me.pugabyte.nexus.utils.BiomeTag;
import org.bukkit.Material;

@Data
@AllArgsConstructor
@ProgressClass(BiomeChallengeProgress.class)
public class BiomeChallenge implements IChallenge {
	private BiomeTag biomeTag;

	@Override
	public Material getDisplayMaterial() {
		return biomeTag.getMaterial();
	}

}
