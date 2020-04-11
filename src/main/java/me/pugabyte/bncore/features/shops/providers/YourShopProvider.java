package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class YourShopProvider extends _ShopProvider {

	public YourShopProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Your shop"))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		Shop shop = new ShopService().get(player);

		contents.set(0, 1, ClickableItem.from(nameItem(Material.ENDER_EYE, "&6Preview your shop"), e -> new PlayerShopProvider(this, shop).open(player)));
		contents.set(0, 2, ClickableItem.from(nameItem(Material.WRITABLE_BOOK, "&6Set shop description"), e ->
				BNCore.getSignMenuFactory().lines(shop.getDescriptionArray()).response((_player, response) -> {
					shop.setDescription(Arrays.asList(response));
					service.save(shop);
					open(player);
				}).open(player)));

		contents.set(0, 4, ClickableItem.from(nameItem(Material.LIME_CONCRETE_POWDER, "&6Add item"), e -> new AddProductProvider(this).open(player)));

		if (!shop.getHolding().isEmpty())
			contents.set(0, 7, ClickableItem.from(nameItem(Material.CYAN_SHULKER_BOX, "&6Collect items"), e -> {}));

		if (shop.getProducts() == null || shop.getProducts().size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		Pagination page = contents.pagination();

		shop.getProducts().forEach(product -> {
			ItemStack item = new ItemBuilder(product.getItem().clone())
					.lore(product.getExchange().getLore(product))
					.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
					.build();

			items.add(ClickableItem.from(item, e -> new EditProductProvider(this, product).open(player)));
		});

		addPagination(player, contents, items);
	}

}
