package me.pugabyte.nexus.features.shops;

import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {

	public static void giveItem(OfflinePlayer player, ItemStack item) {
		if (player.isOnline())
			PlayerUtils.giveItem(player.getPlayer(), item);
		else
			((Shop) new ShopService().get(player)).getHolding().add(item);
	}

}
