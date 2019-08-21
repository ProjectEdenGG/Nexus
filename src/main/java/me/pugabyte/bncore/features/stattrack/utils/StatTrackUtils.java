package me.pugabyte.bncore.features.stattrack.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class StatTrackUtils {

	public static int findItem(PlayerInventory inv, ItemStack item) {
		int slot = -1;
		try {
			for (ItemStack _item : inv.getContents()) {
				++slot;
				if (item.getType() != _item.getType()) continue;

				ItemMeta meta = item.getItemMeta();
				ItemMeta _meta = _item.getItemMeta();
				if (meta.getLore() == null || _meta.getLore() == null) continue;

				List<String> lore = meta.getLore();
				List<String> _lore = _meta.getLore();
				if (lore.size() == 0 || _lore.size() == 0) continue;

				String id = HiddenLore.decode(lore.get(0));
				String _id = HiddenLore.decode(_lore.get(0));
				if (!id.equals(_id)) continue;

				return slot;
			}
		} catch (NullPointerException ex) {
			// Ignore
		}
		return -1;
	}
}
