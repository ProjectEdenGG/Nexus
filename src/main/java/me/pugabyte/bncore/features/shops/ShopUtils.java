package me.pugabyte.bncore.features.shops;

import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.ItemUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {

	public static void giveItem(OfflinePlayer player, ItemStack item) {
		if (player.isOnline())
			ItemUtils.giveItem(player.getPlayer(), item);
		else
			((Shop) new ShopService().get(player)).getHolding().add(item);
	}

}
