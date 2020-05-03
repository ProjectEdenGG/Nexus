package me.pugabyte.bncore.features.shops;

import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.left;
import static me.pugabyte.bncore.utils.StringUtils.trimFirst;

public class ShopUtils {

	public static void giveItem(OfflinePlayer player, ItemStack item) {
		if (player.isOnline())
			Utils.giveItem(player.getPlayer(), item);
		else
			((Shop) new ShopService().get(player)).getHolding().add(item);
	}

	public static String pretty(ItemStack item) {
		return item.getAmount() + " " + camelCase(item.getType().name());
	}

	private static final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();

	public static String pretty(Number price) {
		String format = trimFirst(moneyFormat.format(price));
		if (format.endsWith(".00"))
			format = left(format, format.length() - 3);

		return format;
	}

}
