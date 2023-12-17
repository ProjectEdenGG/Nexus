package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class GlowingEnchant extends CustomEnchant {

	static {
		Tasks.repeat(TimeUtils.TickTime.SECOND.x(5), TimeUtils.TickTime.SECOND.x(5), () -> {
			for (Player player : OnlinePlayers.getAll()) {
				ItemStack helmet = player.getInventory().getHelmet();
				if (helmet == null)
					continue;

				if (!helmet.getItemMeta().hasEnchant(Enchant.GLOWING))
					continue;

				PotionEffect potionEffect = new PotionEffectBuilder()
					.type(PotionEffectType.NIGHT_VISION)
					.duration(TickTime.SECOND.x(30))
					.amplifier(1)
					.ambient(true)
					.particles(false)
					.build();

				player.addPotionEffect(potionEffect);
			}
		});
	}

	@Override
	public @NotNull EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_HEAD;
	}

}
