package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class PlayerShopProvider extends _ShopProvider {
	private Shop shop;

	public PlayerShopProvider(Shop shop, _ShopProvider previousMenu) {
		this.shop = shop;
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0" + shop.getOfflinePlayer().getName() + "'s shop"))
				.size(6, 9)
				.build()
				.open(viewer, page);
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
		page.setItemsPerPage(36);

		shop.getProducts().forEach(product -> {
			ItemStack item = new ItemBuilder(product.getItem().clone()).lore(product.getExchange().getLore(product)).build();
			items.add(ClickableItem.from(item, e -> {
				try {
					product.getExchange().process(product, player);
					open(player, page.getPage());
				} catch (Exception ex) {
					player.sendMessage(colorize(ex.getMessage()));
				}
			}));
		});

		page.setItems(items.toArray(new ClickableItem[0]));
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		if (!page.isFirst())
			contents.set(5, 0, ClickableItem.from(nameItem(new ItemStack(Material.PAPER, Math.max(page.getPage() - 1, 1)),
					"&fPrevious Page"), e -> open(player, page.previous().getPage())));
		if (!page.isLast())
			contents.set(5, 8, ClickableItem.from(nameItem(new ItemStack(Material.PAPER, page.getPage() + 1),
					"&fNext Page"), e -> open(player, page.next().getPage())));
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}
