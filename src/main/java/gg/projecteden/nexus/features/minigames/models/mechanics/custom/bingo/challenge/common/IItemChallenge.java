package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import gg.projecteden.nexus.utils.FuzzyItemStack;
import org.bukkit.Material;

import java.util.Set;

public interface IItemChallenge extends IChallenge {

	Set<FuzzyItemStack> getItems();

	@Override
	default Material getDisplayMaterial() {
		return getItems().iterator().next().getMaterials().iterator().next();
	}

}
