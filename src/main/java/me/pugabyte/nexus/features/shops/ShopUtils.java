package me.pugabyte.nexus.features.shops;

import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ShopUtils {

	public static void giveItems(OfflinePlayer player, ItemStack item) {
		giveItems(player, Collections.singletonList(item));
	}

	public static void giveItems(OfflinePlayer player, List<ItemStack> items) {
		Shop shop = new ShopService().get(player);
		if (player.isOnline())
			PlayerUtils.giveItemsGetExcess(player.getPlayer(), items);
		else
			shop.getHolding().addAll(items);
	}

}
