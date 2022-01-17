package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.BiomeChallengeProgress;
import gg.projecteden.nexus.utils.BiomeTag;
import lombok.AllArgsConstructor;
import lombok.Data;
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
