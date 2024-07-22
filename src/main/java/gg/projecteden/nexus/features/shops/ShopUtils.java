package gg.projecteden.nexus.features.shops;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.pretty;

public class ShopUtils {

	public static void giveItems(UUID uuid, ShopGroup shopGroup, ItemStack item) {
		giveItems(uuid, shopGroup, Collections.singletonList(item));
	}

	public static void giveItems(UUID uuid, ShopGroup shopGroup, List<ItemStack> items) {
		Shop shop = new ShopService().get(uuid);
		if (shop.isOnline() && ShopGroup.of(shop.getOnlinePlayer()) == shopGroup) {
			List<ItemStack> excess = PlayerUtils.giveItemsAndGetExcess(shop.getOnlinePlayer(), items);
			shop.addHolding(shopGroup, excess);
			if (!excess.isEmpty())
				if (new CooldownService().check(uuid, "shop-excess-items", TickTime.SECOND.x(2)))
					PlayerUtils.send(uuid, new JsonBuilder(Shops.PREFIX + "Excess items added to item collection menu, click to view").command("/shops collect"));
		} else
			shop.addHolding(shopGroup, items);
	}

	public static String prettyMoney(Number number) {
		return prettyMoney(number, true);
	}

	public static String prettyMoney(Number number, boolean free) {
		if (free && number.doubleValue() == 0)
			return "Free";
		return "$" + pretty(number);
	}

}
