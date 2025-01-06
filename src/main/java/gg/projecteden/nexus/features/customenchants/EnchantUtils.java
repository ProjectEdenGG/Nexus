package gg.projecteden.nexus.features.customenchants;

import gg.projecteden.api.common.utils.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

public class EnchantUtils {

	public static int getLevel(Enchantment enchantment, ItemStack item) {
		int level = 0;

		if (item != null && item.getItemMeta() != null) {
			if (item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
				if (meta.hasStoredEnchant(enchantment))
					level = meta.getStoredEnchantLevel(enchantment);
			} else {
				if (item.getItemMeta().hasEnchant(enchantment))
					level = item.getItemMeta().getEnchantLevel(enchantment);
			}
		}

		return level;
	}


	public static @NotNull Component displayName(Enchantment enchantment, int level) {
		return Component.text(getDisplayName(enchantment, level));
	}

	@NotNull
	public static String getDisplayName(Enchantment enchantment, int level) {
		return StringUtils.camelCase(enchantment.getKey().getKey()) + (level > 1 ? " " + gg.projecteden.nexus.utils.StringUtils.toRoman(level) : "");
	}

}
