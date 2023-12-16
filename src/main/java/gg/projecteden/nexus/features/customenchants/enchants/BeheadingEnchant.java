package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class BeheadingEnchant extends CustomEnchant {

	public static final int LEVEL_MULTIPLIER = 2;

	public BeheadingEnchant(@NotNull NamespacedKey key) {
		super(key);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

}
