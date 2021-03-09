package me.pugabyte.nexus.models.shop;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.EnumUtils.IteratableEnum;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SerializationUtils.JSON;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.features.shops.ShopUtils.giveItems;
import static me.pugabyte.nexus.features.shops.ShopUtils.prettyMoney;
import static me.pugabyte.nexus.features.shops.Shops.PREFIX;
import static me.pugabyte.nexus.utils.StringUtils.pretty;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

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

	public List<String> getDescription() {
		return description.stream().filter(line -> !isNullOrEmpty(line)).collect(Collectors.toList());
	}

	public void setDescription(List<String> description) {
		this.description = new ArrayList<String>() {{
			for (String line : description)
				if (!isNullOrEmpty(stripColor(line).replace(StringUtils.getColorChar(), "")))
					add(line.startsWith("&") ? line : "&f" + line);
		}};
	}

	public boolean isMarket() {
		return uuid.equals(Nexus.getUUID0());
	}

	public String[] getDescriptionArray() {
		return description.isEmpty() ? new String[]{"", "", "", ""} : description.stream().map(StringUtils::decolorize).toArray(String[]::new);
	}

	public enum ShopGroup {
		SURVIVAL,
		RESOURCE,
		SKYBLOCK,
		ONEBLOCK;

		public static ShopGroup get(org.bukkit.entity.Entity entity) {
			return get(entity.getWorld());
		}

		public static ShopGroup get(World world) {
			return get(world.getName());
		}

		public static ShopGroup get(String world) {
			if (world.toLowerCase().startsWith("resource"))
				return RESOURCE;
			else if (WorldGroup.get(world) == WorldGroup.SKYBLOCK)
				return SKYBLOCK;
			else if (WorldGroup.get(world) == WorldGroup.ONEBLOCK)
				return ONEBLOCK;
			else if (WorldGroup.get(world) == WorldGroup.SURVIVAL)
				return SURVIVAL;

			return null;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	@Converters({UUIDConverter.class, ItemStackConverter.class})
	public static class Product {
		@NonNull
		private UUID uuid;
		@NonNull
		private ShopGroup shopGroup;
		@Embedded
		private ItemStack item;
		private double stock;
		private ExchangeType exchangeType;
		private Object price;

		@PostLoad
		void fix(DBObject dbObject) {
			if (!(price instanceof Number))
				price = JSON.deserializeItemStack((Map<String, Object>) dbObject.get("price"));
		}

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
			if (uuid.equals(customer.getUniqueId()))
				throw new InvalidInputException("You cannot buy items from yourself");

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

			Nexus.csvLog("exchange", String.join(",", columns));
		}

		@NotNull
		public Exchange getExchange() {
			return exchangeType.init(price);
		}

		public ItemStack getOwnLore() {
			return new ItemBuilder(item.clone())
					.lore(getExchange().getOwnLore(this))
					.lore("", "&7Click to edit")
					.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
					.build();
		}

		public boolean isMarket() {
			return getShop().isMarket();
		}

		public void addStock(int amount) {
			stock += amount;
		}

		public void removeStock(int amount) {
			stock -= amount;
		}

		public List<ItemStack> getItemStacks() {
			return getItemStacks(-1);
		}

		public List<ItemStack> getItemStacks(int maxStacks) {
			List<ItemStack> items = new ArrayList<>();

			ItemStack item = this.item.clone();
			double stock = this.stock;
			int maxStackSize = item.getMaxStackSize();

			while (stock > 0) {
				if (maxStacks > 0 && items.size() > maxStacks)
					break;

				ItemStack next = new ItemStack(item.clone());
				next.setAmount((int) Math.min(maxStackSize, stock));
				stock -= next.getAmount();
				items.add(next);
			}

			return items;
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
			if (!Nexus.getEcon().has(customer, price))
				throw new InvalidInputException("You do not have enough money to purchase this item");

			product.setStock(product.getStock() - product.getItem().getAmount());
			if (price > 0) {
				Nexus.getEcon().withdrawPlayer(customer, price);
				if (!product.isMarket())
					Nexus.getEcon().depositPlayer(product.getShop().getOfflinePlayer(), price);
			}
			giveItems(customer, product.getItem());
			new ShopService().save(product.getShop());
			PlayerUtils.send(customer, PREFIX + "You purchased " + pretty(product.getItem()) + " for " + prettyMoney(price));
		}

		@Override
		public List<String> getLore(Product product) {
			int stock = (int) product.getStock();
			String desc = "&7Buy &e" + product.getItem().getAmount() + " &7for &a" + prettyMoney(price);

			if (product.getUuid().equals(Nexus.getUUID0()))
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
					"&7Selling &e" + product.getItem().getAmount() + " &7for &a" + prettyMoney(price),
					"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock
			);
		}
	}

	@Data
	@Builder
	// Customer buying an item from the shop owner for other items
	public static class TradeExchange implements Exchange {
		@NonNull
		private ItemStack price;

		public TradeExchange(@NonNull Object price) {
			this.price = (ItemStack) price;
		}

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
			if (!product.isMarket())
				product.getShop().getHolding().add(price);
			giveItems(customer, product.getItem());
			new ShopService().save(product.getShop());
			PlayerUtils.send(customer, PREFIX + "You purchased " + pretty(product.getItem()) + " for " + pretty(price));
		}

		@Override
		public List<String> getLore(Product product) {
			int stock = (int) product.getStock();
			String desc = "&7Buy &e" + product.getItem().getAmount() + " &7for &a" + pretty(price);
			if (product.getUuid().equals(Nexus.getUUID0()))
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
					"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock
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
			if (!product.isMarket() && !Nexus.getEcon().has(shopOwner, price))
				throw new InvalidInputException(shopOwner.getName() + " does not have enough money to purchase this item from you");
			if (!customer.getInventory().containsAtLeast(product.getItem(), product.getItem().getAmount()))
				throw new InvalidInputException("You do not have " + pretty(product.getItem()) + " to sell");

			product.setStock(product.getStock() - price);
			if (price > 0) {
				if (!product.isMarket())
					Nexus.getEcon().withdrawPlayer(shopOwner, price);
				Nexus.getEcon().depositPlayer(customer, price);
			}
			customer.getInventory().removeItem(product.getItem());
			if (!product.isMarket())
				product.getShop().getHolding().add(product.getItem());
			new ShopService().save(product.getShop());
			PlayerUtils.send(customer, PREFIX + "You sold " + pretty(product.getItem()) + " for " + prettyMoney(price));
		}

		@Override
		public List<String> getLore(Product product) {
			String desc = "&7Sell &e" + product.getItem().getAmount() + " &7for &a" + prettyMoney(price);
			if (product.getUuid().equals(Nexus.getUUID0()))
				return Arrays.asList(
						desc,
						"&7Seller: &6Market"
				);
			else
				return Arrays.asList(
						desc,
						"&7Stock: &e" + prettyMoney(product.getStock()),
						"&7Seller: &e" + product.getShop().getOfflinePlayer().getName()
				);
		}

		@Override
		public List<String> getOwnLore(Product product) {
			return Arrays.asList(
					"&7Buying &e" + product.getItem().getAmount() + " &7for &a" + prettyMoney(price),
					"&7Stock: &e" + prettyMoney(product.getStock())
			);
		}
	}

}
