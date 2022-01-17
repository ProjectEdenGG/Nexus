package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.DimensionChallengeProgress;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.World.Environment;

@Data
@AllArgsConstructor
@ProgressClass(DimensionChallengeProgress.class)
public class DimensionChallenge implements IChallenge {
	private Environment dimension;

	@Override
	public Material getDisplayMaterial() {
		return ItemUtils.getDimensionDisplayMaterial(dimension);
	}

}
