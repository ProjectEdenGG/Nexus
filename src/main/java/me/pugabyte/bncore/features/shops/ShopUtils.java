package me.pugabyte.bncore.features.shops;

import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {

	public static void giveItem(OfflinePlayer player, ItemStack item) {
		if (player.isOnline())
			Utils.giveItem(player.getPlayer(), item);
		else
			((Shop) new ShopService().get(player)).getHolding().add(item);
	}

}
