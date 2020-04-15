package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class PlayerShopProvider extends _ShopProvider {
	private Shop shop;

	public PlayerShopProvider(_ShopProvider previousMenu, Shop shop) {
		this.previousMenu = previousMenu;
		this.shop = shop;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0" + shop.getOfflinePlayer().getName() + "'s shop");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);
		addItems(player, contents);
	}

	public void addItems(Player player, InventoryContents contents) {
		if (shop.getProducts() == null || shop.getProducts().size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		Pagination page = contents.pagination();

		shop.getProducts().forEach(product -> {
			ItemStack item = new ItemBuilder(product.getItem().clone())
					.lore(product.getExchange().getLore(product))
					.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
					.build();

			items.add(ClickableItem.from(item, e -> {
				try {
					product.getExchange().process(product, player);
					open(player, page.getPage());
				} catch (Exception ex) {
					player.sendMessage(colorize(ex.getMessage()));
				}
			}));
		});

		addPagination(player, contents, items);
	}

}
