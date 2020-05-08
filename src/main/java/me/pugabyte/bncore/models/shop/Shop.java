package me.pugabyte.bncore.models.shop;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemMetaConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.Utils.IteratableEnum;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.shops.ShopUtils.giveItem;
import static me.pugabyte.bncore.features.shops.ShopUtils.pretty;
import static me.pugabyte.bncore.features.shops.Shops.PREFIX;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
@Builder
@Entity("shop")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class, ItemMetaConverter.class})
public class Shop extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<String> description = new ArrayList<>();
	@Embedded
	private List<Product> products = new ArrayList<>();
	@Embedded
	private List<ItemStack> holding = new ArrayList<>();
	// TODO holding for money, maybe? would make withdrawing money more complicated
	// private double profit;

	public List<Product> getProducts(ShopGroup shopGroup) {
		return products.stream().filter(product -> product.getShopGroup().equals(shopGroup)).collect(Collectors.toList());
	}

	public String[] getDescriptionArray() {
		return description.isEmpty() ? new String[]{"", "", "", ""} : description.toArray(new String[0]);
	}

	public enum ShopGroup {
		SURVIVAL,
		RESOURCE,
		SKYBLOCK;

		public static ShopGroup get(org.bukkit.entity.Entity entity) {
			return get(entity.getWorld());
		}

		public static ShopGroup get(World world) {
			return get(world.getName());
		}

		public static ShopGroup get(String world) {
			if (world.toLowerCase().startsWith("resource"))
				return RESOURCE;
			else if (world.toLowerCase().startsWith("skyblock"))
				return SKYBLOCK;
			else if (WorldGroup.get(world) == WorldGroup.SURVIVAL)
				return SURVIVAL;

			return null;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({UUIDConverter.class, ItemStackConverter.class, ItemMetaConverter.class})
	public static class Product {
		private UUID uuid;
		private ShopGroup shopGroup;
		@Embedded
		private ItemStack item;
		private double stock;
		private ExchangeType exchangeType;
		private Object price;

		public Shop getShop() {
			return new ShopService().get(uuid);
		}

		public ItemStack getItem() {
			return item.clone();
		}

		public void setStock(double stock) {
			if (this.stock == -1)
				return;
			this.stock = Math.max(stock, 0);
		}

		@SneakyThrows
		public void process(Player customer) {
			getExchange().process(this, customer);
			log(customer);
		}

		public void log(Player customer) {
			List<String> columns = new ArrayList<>(Arrays.asList(
					DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
					getUuid().toString(),
					getShop().getOfflinePlayer().getName(),
					customer.getUniqueId().toString(),
					customer.getName(),
					getShopGroup().name(),
					item.getType().name(),
					String.valueOf(item.getAmount()),
					exchangeType.name()
			));

			if (price instanceof ItemStack) {
				columns.add(((ItemStack) price).getType().name());
				columns.add(String.valueOf(((ItemStack) price).getAmount()));
			} else {
				columns.add(String.valueOf(price));
				columns.add("");
			}

			BNCore.csvLog("exchange", String.join(",", columns));
		}

		@NotNull
		public Exchange getExchange() {
			return exchangeType.init(price);
		}
	}

	// Dumb enum due to morphia refusing to deserialize interfaces properly
	public enum ExchangeType implements IteratableEnum {
		SELL(SellExchange.class),
		TRADE(TradeExchange.class),
		BUY(BuyExchange.class);

		@Getter
		private Class<? extends Exchange> clazz;

		ExchangeType(Class<? extends Exchange> clazz) {
			this.clazz = clazz;
		}

		@SneakyThrows
		public Exchange init(Object price) {
			return (Exchange) clazz.getDeclaredConstructors()[0].newInstance(price);
		}
	}

	public interface Exchange {

		void process(Product product, Player customer);

		List<String> getLore(Product product);
		List<String> getOwnLore(Product product);

	}

	@Data
	@Builder
	@AllArgsConstructor
	// Customer buying an item from the shop owner for money
	public static class SellExchange implements Exchange {
		@NonNull
		private Double price;

		@Override
		public void process(Product product, Player customer) {
			if (customer.getUniqueId() == product.getShop().getUuid())
				throw new InvalidInputException("You cannot purchase from your own shop");
			if (product.getStock() == 0)
				throw new InvalidInputException("This item is out of stock");
			if (!BNCore.getEcon().has(customer, price))
				throw new InvalidInputException("You do not have enough money to purchase this item");

			product.setStock(product.getStock() - product.getItem().getAmount());
			BNCore.getEcon().withdrawPlayer(customer, price);
			if (!isMarket(product))
				BNCore.getEcon().depositPlayer(product.getShop().getOfflinePlayer(), price);
			giveItem(customer, product.getItem());
			new ShopService().save(product.getShop());
			customer.sendMessage(colorize(PREFIX + "You purchased " + pretty(product.getItem()) + " for $" + pretty(price)));
		}

		@Override
		public List<String> getLore(Product product) {
			int stock = (int) product.getStock();
			String desc = "&7Buy &e" + product.getItem().getAmount() + " &7for &a$" + pretty(price);

			if (product.getUuid().equals(BNCore.getUUID0()))
				return Arrays.asList(
						desc,
						"&7Seller: &6Market"
				);
			else
				return Arrays.asList(
						desc,
						"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock,
						"&7Seller: &e" + product.getShop().getOfflinePlayer().getName()
				);
		}

		@Override
		public List<String> getOwnLore(Product product) {
			int stock = (int) product.getStock();
			return Arrays.asList(
					"&7Selling &e" + product.getItem().getAmount() + " &7for &a$" + pretty(price),
					"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock,
					"",
					"&7Click to edit"
			);
		}
	}

	@Data
	@Builder
	@AllArgsConstructor
	// Customer buying an item from the shop owner for other items
	public static class TradeExchange implements Exchange {
		@NonNull
		private ItemStack price;

		@Override
		public void process(Product product, Player customer) {
			if (customer.getUniqueId() == product.getShop().getUuid())
				throw new InvalidInputException("You cannot purchase from your own shop");
			if (product.getStock() == 0)
				throw new InvalidInputException("This item is out of stock");
			if (product.getStock() < product.getItem().getAmount())
				throw new InvalidInputException("There is not enough stock to fulfill your purchase");
			if (!customer.getInventory().containsAtLeast(price, price.getAmount()))
				throw new InvalidInputException("You do not have " + pretty(price) + " to purchase this item");

			product.setStock(product.getStock() - product.getItem().getAmount());
			customer.getInventory().removeItem(price);
			if (!isMarket(product))
				product.getShop().getHolding().add(price);
			giveItem(customer, product.getItem());
			new ShopService().save(product.getShop());
			customer.sendMessage(colorize(PREFIX + "You purchased " + pretty(product.getItem()) + " for " + pretty(price)));
		}

		@Override
		public List<String> getLore(Product product) {
			int stock = (int) product.getStock();
			String desc = "&7Buy &e" + product.getItem().getAmount() + " &7for &a" + pretty(price);
			if (product.getUuid().equals(BNCore.getUUID0()))
				return Arrays.asList(
						desc,
						"&7Seller: &6Market"
				);
			else
				return Arrays.asList(
						desc,
						"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock,
						"&7Seller: &e" + product.getShop().getOfflinePlayer().getName()
				);
		}

		@Override
		public List<String> getOwnLore(Product product) {
			int stock = (int) product.getStock();
			return Arrays.asList(
					"&7Selling &e" + product.getItem().getAmount() + " &7for &a" + pretty(price),
					"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock,
					"",
					"&7Click to edit"
			);
		}
	}

	@Data
	@Builder
	@AllArgsConstructor
	// Customer selling an item to the shop owner for money
	public static class BuyExchange implements Exchange {
		@NonNull
		private Double price;

		@Override
		public void process(Product product, Player customer) {
			OfflinePlayer shopOwner = product.getShop().getOfflinePlayer();
			if (customer.getUniqueId() == product.getShop().getUuid())
				throw new InvalidInputException("You cannot purchase from your own shop");
			if (product.getStock() == 0)
				throw new InvalidInputException("This item is out of stock");
			if (product.getStock() > 0 && product.getStock() < price)
				throw new InvalidInputException("There is not enough stock to fulfill your purchase");
			if (!isMarket(product) && !BNCore.getEcon().has(shopOwner, price))
				throw new InvalidInputException(shopOwner.getName() + " does not have enough money to purchase this item from you");
			if (!customer.getInventory().containsAtLeast(product.getItem(), product.getItem().getAmount()))
				throw new InvalidInputException("You do not have " + pretty(product.getItem()) + " to sell");

			product.setStock(product.getStock() - price);
			if (!isMarket(product))
				BNCore.getEcon().withdrawPlayer(shopOwner, price);
			BNCore.getEcon().depositPlayer(customer, price);
			customer.getInventory().removeItem(product.getItem());
			if (!isMarket(product))
				product.getShop().getHolding().add(product.getItem());
			new ShopService().save(product.getShop());
			customer.sendMessage(colorize(PREFIX + "You sold " + pretty(product.getItem()) + " for $" + pretty(price)));
		}

		@Override
		public List<String> getLore(Product product) {
			String desc = "&7Sell &e" + product.getItem().getAmount() + " &7for &a$" + pretty(price);
			if (product.getUuid().equals(BNCore.getUUID0()))
				return Arrays.asList(
						desc,
						"&7Seller: &6Market"
				);
			else
				return Arrays.asList(
						desc,
						"&7Stock: &e$" + pretty(product.getStock()),
						"&7Seller: &e" + product.getShop().getOfflinePlayer().getName()
				);
		}

		@Override
		public List<String> getOwnLore(Product product) {
			return Arrays.asList(
					"&7Buying &e" + product.getItem().getAmount() + " &7for &a$" + pretty(price),
					"&7Stock: &e$" + pretty(product.getStock()),
					"",
					"&7Click to edit"
			);
		}
	}

	public static boolean isMarket(Product product) {
		return product.getUuid().equals(BNCore.getUUID0());
	}

}
