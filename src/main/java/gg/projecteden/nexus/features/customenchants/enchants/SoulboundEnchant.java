package gg.projecteden.nexus.features.customenchants.enchants;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.utils.Enchant;
import lombok.NoArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SoulboundEnchant extends CustomEnchant {
	public static final String NBT_KEY = "soulbound";

	public SoulboundEnchant(@NotNull NamespacedKey key) {
		super(key);
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@NoArgsConstructor
	public static class SoulboundListener implements Listener {
		private static final CustomEnchant enchant = Enchant.SOULBOUND;

		@EventHandler
		public void onPlayerDeath(PlayerDeathEvent event) {
			final Iterator<ItemStack> drops = event.getDrops().iterator();
			while (drops.hasNext()) {
				ItemStack drop = drops.next();
				int level = drop.getEnchantmentLevel(enchant);
				if (level <= 0)
					continue;

				if (--level == 0)
					drop.removeEnchantment(enchant);
				else
					drop.addUnsafeEnchantment(enchant, level);

				CustomEnchants.update(drop);

				drops.remove();
				event.getItemsToKeep().add(drop);
			}
		}

		@EventHandler
		public void onPlayerDeath_nbt(PlayerDeathEvent event) {
			final Iterator<ItemStack> drops = event.getDrops().iterator();
			while (drops.hasNext()) {
				ItemStack drop = drops.next();
				final NBTItem nbtItem = new NBTItem(drop);

				if (!nbtItem.hasKey(NBT_KEY))
					continue;

				if (!nbtItem.getBoolean(NBT_KEY))
					continue;

				drops.remove();
				event.getItemsToKeep().add(drop);
			}
		}

	}

}
