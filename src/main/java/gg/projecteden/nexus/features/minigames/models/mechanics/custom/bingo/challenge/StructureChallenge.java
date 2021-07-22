package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.StructureChallengeProgress;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.StructureType;

@Data
@AllArgsConstructor
@ProgressClass(StructureChallengeProgress.class)
public class StructureChallenge implements IChallenge {
	private StructureType structureType;

	@Override
	public Material getDisplayMaterial() {
		return ItemUtils.getStructureTypeDisplayMaterial(structureType);
	}

}
