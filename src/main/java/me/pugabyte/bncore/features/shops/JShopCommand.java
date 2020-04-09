package me.pugabyte.bncore.features.shops;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.Shop.ItemForItemExchange;
import me.pugabyte.bncore.models.shop.Shop.ItemForMoneyExchange;
import me.pugabyte.bncore.models.shop.Shop.MoneyForItemExchange;
import me.pugabyte.bncore.models.shop.Shop.Product;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
		shop.getItems().add(new Product(shop.getUuid(), new ItemBuilder(Material.COBBLESTONE).amount(32).build(), 512, new ItemForMoneyExchange(4D)));
		shop.getItems().add(new Product(shop.getUuid(), new ItemBuilder(Material.DIAMOND_SWORD).amount(1).build(), 16, new ItemForMoneyExchange(50D)));
		shop.getItems().add(new Product(shop.getUuid(), new ItemBuilder(Material.IRON_ORE).amount(10).build(), 100, new ItemForItemExchange(new ItemStack(Material.GOLD_ORE, 3))));
		shop.getItems().add(new Product(shop.getUuid(), new ItemBuilder(Material.DIAMOND).amount(10).build(), 500, new MoneyForItemExchange(100D)));
		service.save(shop);
	}

}
