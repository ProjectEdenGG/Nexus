package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class GlowingEnchant extends CustomEnchant {

	public GlowingEnchant(@NotNull NamespacedKey key) {
		super(key);
	}

	static {
		Tasks.repeat(TimeUtils.TickTime.SECOND.x(5), TimeUtils.TickTime.SECOND.x(5), () -> {
			for (Player player : PlayerUtils.getOnlinePlayers()) {
				if (player.getInventory().getHelmet() != null) {
					if (player.getInventory().getHelmet().getItemMeta().hasEnchant(Enchant.GLOWING)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, TimeUtils.TickTime.SECOND.x(30), 1, true, false));
					}
				}
			}
		});
	}

	@Override
	public @NotNull EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_HEAD;
	}
}
