package me.pugabyte.bncore.features.shops;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.Shop.ExchangeType;
import me.pugabyte.bncore.models.shop.Shop.Product;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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

	@Path("addItems1")
	void addItems1() {
		Shop shop = service.get(player());
		shop.getProducts().clear();
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.COBBLESTONE).amount(32).build(), 512, ExchangeType.ITEM_FOR_MONEY, 8.5D));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.DIAMOND_SWORD).amount(1).build(), 16, ExchangeType.ITEM_FOR_MONEY, 50D));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.IRON_ORE).amount(10).build(), 100, ExchangeType.ITEM_FOR_ITEM, new ItemStack(Material.GOLD_ORE, 3)));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.DIAMOND).amount(10).build(), 500, ExchangeType.MONEY_FOR_ITEM, 100D));
		service.save(shop);
	}

	@Path("addItems2")
	void addItems2() {
		Shop shop = service.get(player());
		shop.getProducts().clear();
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.SPRUCE_LOG).amount(32).build(), 100, ExchangeType.ITEM_FOR_MONEY, 16D));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.IRON_AXE).amount(1).build(), 16, ExchangeType.ITEM_FOR_MONEY, 30D));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.GOLD_INGOT).amount(10).build(), 100, ExchangeType.ITEM_FOR_ITEM, new ItemStack(Material.GOLDEN_APPLE)));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.DIAMOND).amount(10).build(), -1, ExchangeType.MONEY_FOR_ITEM, 90D));
		service.save(shop);
	}

	@Path("addItems3")
	void addItems3() {
		Shop shop = service.get(player());
		shop.getProducts().clear();
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.SAND).amount(32).build(), 2000, ExchangeType.ITEM_FOR_MONEY, 8.5D));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_INFINITE).amount(1).build(), 1, ExchangeType.ITEM_FOR_MONEY, 1000D));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.COOKED_BEEF).amount(10).build(), 100, ExchangeType.ITEM_FOR_ITEM, new ItemStack(Material.BEEF, 10)));
		shop.getProducts().add(new Product(shop.getUuid(), new ItemBuilder(Material.PURPLE_WOOL).amount(10).build(), 10000, ExchangeType.MONEY_FOR_ITEM, 3D));
		service.save(shop);
	}

}
