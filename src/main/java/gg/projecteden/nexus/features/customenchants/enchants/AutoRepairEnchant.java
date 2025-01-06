package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class AutoRepairEnchant extends CustomEnchant {

	static {
		Tasks.repeat(TickTime.SECOND.x(3), TickTime.SECOND.x(1.5), () -> {
			for (Player player : OnlinePlayers.getAll()) {
				if (AFK.get(player).isAfk())
					continue;

				for (ItemStack item : getItems(player.getInventory())) {
					ItemMeta meta = item.getItemMeta();
					if (meta == null)
						continue;

					if (!(meta instanceof Damageable damageable))
						continue;

					int damage = damageable.getDamage();
					int level = item.getItemMeta().getEnchantLevel(Enchant.AUTOREPAIR);

					if (level == 0)
						continue;

					if (damageable.getDamage() == 0)
						continue;

					damageable.setDamage(Math.max(0, damage - level));
					item.setItemMeta(meta);
				}
			}
		});
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}
}
