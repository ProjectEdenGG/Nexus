package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.commands.DurabilityWarningCommand;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoRepairEnchant extends CustomEnchant {

	public AutoRepairEnchant(@NotNull NamespacedKey key) {
		super(key);
	}

	static {
		Tasks.repeat(TimeUtils.Time.SECOND.x(3), TimeUtils.Time.SECOND.x(3), () -> {
			for (Player player : PlayerUtils.getOnlinePlayers()) {
				if (AFK.get(player).isAfk()) continue;
				for (ItemStack item : getItems(player.getInventory())) {
					ItemMeta meta = item.getItemMeta();
					if (meta == null) {
						continue;
					}
					if (!(meta instanceof Damageable)) continue;
					int damage = ((Damageable) meta).getDamage();
					int level = getEnchLevel(item);

					if (level == 0) {
						continue;
					}
					if (((Damageable) meta).getDamage() == 0) continue;
					((Damageable) meta).setDamage(Math.max(0, damage - level));
					item.setItemMeta(meta);
				}
			}
		});
	}

	private static int getEnchLevel(ItemStack item) {
		return item.getItemMeta().getEnchantLevel(Enchant.AUTOREPAIR);
	}

	@NotNull
	private static List<ItemStack> getItems(PlayerInventory inventory) {
		List<ItemStack> items = new ArrayList<>() {{
			addAll(Arrays.asList(inventory.getArmorContents()));
			add(inventory.getItemInMainHand());
			add(inventory.getItemInOffHand());
		}};

		items.removeIf(ItemUtils::isNullOrAir);
		return items;
	}

}
