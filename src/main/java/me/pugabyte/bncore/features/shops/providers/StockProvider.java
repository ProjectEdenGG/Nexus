package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.bncore.models.shop.Shop.Product;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public class StockProvider extends _ShopProvider {
	private Product product;

	public StockProvider(_ShopProvider previousMenu, Product product) {
		this.previousMenu = previousMenu;
		this.product = product;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Edit Stock");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fillRow(0, ClickableItem.empty(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)));
		super.init(player, contents);

		IntStream.range(1, 5).forEach(i -> IntStream.range(0, 9).forEach(i2 -> {
			SlotPos slot = new SlotPos(i, i2);
			contents.set(slot, ClickableItem.empty(new ItemStack(Material.GOLD_INGOT)));
			if (i2 < 7)
				contents.setEditable(slot, true);
		}));

	}

}
