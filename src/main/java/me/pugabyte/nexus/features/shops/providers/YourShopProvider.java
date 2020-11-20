package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.shops.Shops;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YourShopProvider extends _ShopProvider {

	public YourShopProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Your shop");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		Shop shop = new ShopService().get(player);

		contents.set(0, 1, ClickableItem.from(nameItem(Material.ENDER_EYE, "&6Preview your shop"), e -> new PlayerShopProvider(this, shop).open(player)));
		contents.set(0, 2, ClickableItem.from(nameItem(Material.OAK_SIGN, "&6Set shop description"), e ->
				Nexus.getSignMenuFactory().lines(shop.getDescriptionArray()).prefix(Shops.PREFIX).response(lines -> {
					shop.setDescription(Arrays.asList(lines));
					service.save(shop);
					open(player);
				}).open(player)));

		contents.set(0, 4, ClickableItem.from(nameItem(Material.LIME_CONCRETE_POWDER, "&6Add item"), e -> new AddProductProvider(this).open(player)));

		contents.set(0, 6, ClickableItem.from(nameItem(Material.WRITABLE_BOOK, "Shop history"), e -> {}));
		contents.set(0, 7, ClickableItem.from(nameItem(Material.CYAN_SHULKER_BOX, "&6Collect items"), e -> {}));

		if (shop.getProducts() == null || shop.getProducts().size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		shop.getProducts(ShopGroup.get(player)).forEach(product -> {
			ItemStack item = new ItemBuilder(product.getItem().clone())
					.lore(product.getExchange().getOwnLore(product))
					.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
					.build();

			items.add(ClickableItem.from(item, e -> new EditProductProvider(this, product).open(player)));
		});

		addPagination(player, contents, items);
	}

}
