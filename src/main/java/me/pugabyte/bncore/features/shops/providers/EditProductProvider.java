package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.Shop.Product;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EditProductProvider extends _ShopProvider {
	private Product product;

	public EditProductProvider(_ShopProvider previousMenu, Product product) {
		this.previousMenu = previousMenu;
		this.product = product;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Browse Item");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		contents.set(0, 4, ClickableItem.from(new ItemBuilder(Material.CHEST).name("&6Edit Stock").build(), e ->
				new StockProvider(this, product).open(player)));
		contents.set(0, 6, ClickableItem.from(new ItemBuilder(Material.LAVA_BUCKET).name("&cDelete").build(), e ->
				ConfirmationMenu.builder()
						.onConfirm(e2 -> {
							Shop shop = service.get(player);
							shop.getProducts().remove(product);
							service.save(shop);
							previousMenu.open(player);
						})
						.onCancel(e2 -> open(player))
						.open(player)));

	}

}
