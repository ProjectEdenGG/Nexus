package me.pugabyte.bncore.features.shops;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.Shop.ItemForMoneyExchange;
import me.pugabyte.bncore.models.shop.Shop.ShopItem;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;

@Permission("group.staff")
@Aliases("jshops")
public class JShopCommand extends CustomCommand {
	private ShopService service = new ShopService();

	public JShopCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		ShopMenu.MAIN_MENU.open(player(), null);
	}

	@Path("addItems")
	void addItems() {
		Shop shop = service.get(player());
		shop.getItems().clear();
		shop.getItems().add(new ShopItem(shop.getUuid(), new ItemBuilder(Material.COBBLESTONE).amount(32).build(), 512, new ItemForMoneyExchange(4)));
		shop.getItems().add(new ShopItem(shop.getUuid(), new ItemBuilder(Material.DIAMOND_SWORD).amount(1).build(), 16, new ItemForMoneyExchange(50)));
		service.save(shop);
	}

}
