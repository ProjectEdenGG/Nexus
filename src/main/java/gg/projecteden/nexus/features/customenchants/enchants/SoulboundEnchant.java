package gg.projecteden.nexus.features.customenchants.enchants;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SoulboundEnchant extends CustomEnchant implements Listener {
	public static final String NBT_KEY = "soulbound";

	@Override
	public @NotNull String getDisplayName(int level) {
		if (level >= getMaxLevel())
			return StringUtils.camelCase(getName()) + " ∞";

		return super.getDisplayName(level);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Iterator<ItemStack> drops = event.getDrops().iterator();
		while (drops.hasNext()) {
			ItemStack drop = drops.next();
			ItemMeta meta = drop.getItemMeta();
			int level = drop.getItemMeta().getEnchantLevel(this);
			if (level <= 0)
				continue;

			if (level < getMaxLevel())
				--level;

			if (level == 0)
				meta.removeEnchant(this);
			else
				meta.addEnchant(this, level, true);

			drop.setItemMeta(meta);
			CustomEnchants.update(drop, event.getPlayer());

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
			if (!nbtItem.hasKey(NBT_KEY) || !nbtItem.getBoolean(NBT_KEY))
				continue;

			drops.remove();
			event.getItemsToKeep().add(drop);
		}
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}
}
