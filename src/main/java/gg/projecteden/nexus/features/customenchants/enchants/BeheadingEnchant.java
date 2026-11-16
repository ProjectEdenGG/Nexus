package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeheadingEnchant extends CustomEnchant {

	public static final int LEVEL_MULTIPLIER = 2;

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public List<Material> getSupportedMaterials() {
		return MaterialTag.AXES.getValues().stream().toList();
	}
}
