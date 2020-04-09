package me.pugabyte.bncore.models.shop;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
@Builder
@Entity("shop")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class Shop extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String description;
	@Embedded
	private List<Product> items = new ArrayList<>();
	@Embedded
	private List<ItemStack> holding = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Product {
		private UUID uuid;
		private ItemStack item;
		private double stock;
		private Exchange exchange;

		public Shop getShop() {
			return new ShopService().get(uuid);
		}
	}

	public interface Exchange {

		<T> T getPrice();

		void process(Product product, Player customer);

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	// Customer buying an item from the shop owner for money
	public static class ItemForMoneyExchange implements Exchange {
		@NonNull
		private Double price;

		@Override
		public void process(Product product, Player customer) {
			BNCore.log("Processing ItemForMoneyExchange");
			if (product.getStock() == 0)
				throw new InvalidInputException("This item is out of stock");
			if (!BNCore.getEcon().has(customer, price))
				throw new InvalidInputException("You do not have enough money to purchase this item");

			BNCore.getEcon().withdrawPlayer(customer, price);
			BNCore.getEcon().depositPlayer(product.getShop().getOfflinePlayer(), price);
			giveItem(customer, product.getItem());
			product.setStock(product.getStock() - product.getItem().getAmount());
			new ShopService().save(product.getShop());
			customer.sendMessage(colorize("You purchased " + product.getItem().getType() + " x" + product.getItem().getAmount() + " for $" + price));
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	// Customer buying an item from the shop owner for other items
	public static class ItemForItemExchange implements Exchange {
		@NonNull
		private ItemStack price;

		@Override
		public void process(Product product, Player customer) {
			BNCore.log("Processing ItemForItemExchange");
			if (product.getStock() == 0)
				throw new InvalidInputException("This item is out of stock");
			if (product.getStock() < product.getItem().getAmount())
				throw new InvalidInputException("There is not enough stock to fulfill your purchase");
			if (!customer.getInventory().contains(price))
				throw new InvalidInputException("You do not have enough " + pretty(price) + " to purchase this item");

			customer.getInventory().remove(price);
			giveItem(product.getShop().getOfflinePlayer(), price);
			giveItem(customer, product.getItem());
			product.setStock(product.getStock() - product.getItem().getAmount());
			new ShopService().save(product.getShop());
			customer.sendMessage(colorize("You purchased " + pretty(product.getItem()) + " for " + pretty(price)));
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	// Customer selling an item to the shop owner for money
	public static class MoneyForItemExchange implements Exchange {
		@NonNull
		private Double price;

		@Override
		public void process(Product product, Player customer) {
			BNCore.log("Processing ItemForMoneyExchange");
			OfflinePlayer shopOwner = product.getShop().getOfflinePlayer();
			if (product.getStock() == 0)
				throw new InvalidInputException("This item is out of stock");
			if (product.getStock() > 0 && product.getStock() < price)
				throw new InvalidInputException("There is not enough stock to fulfill your purchase");
			if (!BNCore.getEcon().has(shopOwner, price))
				throw new InvalidInputException(shopOwner.getName() + " does not have enough money to purchase this item from you");
			if (!customer.getInventory().contains(product.getItem()))
				throw new InvalidInputException("You do not have enough " + pretty(product.getItem()) + " to sell");

			BNCore.getEcon().withdrawPlayer(shopOwner, price);
			BNCore.getEcon().depositPlayer(customer, price);
			giveItem(shopOwner, product.getItem());
			customer.getInventory().remove(product.getItem());
			product.setStock(product.getStock() - price);
			new ShopService().save(product.getShop());
			customer.sendMessage(colorize("You sold " + product.getItem().getType() + " x" + product.getItem().getAmount() + " for $" + price));
		}
	}

	// TODO move
	public static void giveItem(OfflinePlayer player, ItemStack item) {
		if (player.isOnline())
			Utils.giveItem(player.getPlayer(), item);
		else
			((Shop) new ShopService().get(player)).getHolding().add(item);
	}

	private static String pretty(ItemStack price) {
		return price.getType() + " x" + price.getAmount();
	}

}
