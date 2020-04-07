package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class PlayerShopsProvider extends _ShopProvider {

	public PlayerShopsProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Player Shops"))
				.size(6, 9)
				.build()
				.open(viewer);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		ItemStack pugabyte = new ItemBuilder(Material.PLAYER_HEAD).skullOwner("Pugabyte").build();
		contents.set(1, 0, ClickableItem.from(pugabyte, e -> new PlayerShopProvider(new ShopService().get(Utils.getPlayer("Pugabyte")), this).open(player)));
	}


}
