package gg.projecteden.nexus.models.shop;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.itemtags.ItemTagsUtils;
import gg.projecteden.nexus.features.shops.ShopUtils;
import gg.projecteden.nexus.features.shops.providers.ResourceWorldMarketProvider.AutoSellBehavior;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Axolotl.Variant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;
import static gg.projecteden.nexus.features.shops.ShopUtils.giveItems;
import static gg.projecteden.nexus.features.shops.ShopUtils.prettyMoney;
import static gg.projecteden.nexus.features.shops.Shops.PREFIX;
import static gg.projecteden.nexus.utils.ItemUtils.getShulkerContents;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.PlayerUtils.hasRoomFor;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.pretty;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Data
@Entity(value = "shop", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class Shop implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<String> description = new ArrayList<>();
	@Embedded
	private List<Product> products = new ArrayList<>();
	@Embedded
	private Map<ShopGroup, List<ItemStack>> holding = new ConcurrentHashMap<>();
	@Embedded
	private List<Material> disabledResourceMarketItems = new ArrayList<>();
	private AutoSellBehavior resourceMarketAutoSellBehavior = AutoSellBehavior.INDIVIDUAL;

	// TODO holding for money, maybe? would make withdrawing money more complicated
	// private double profit;

	public List<Product> getProducts(ShopGroup shopGroup) {
		return products.stream().filter(product -> product.getShopGroup().equals(shopGroup)).collect(Collectors.toList());
	}

	public List<String> getDescription() {
		return description.stream().filter(Nullables::isNotNullOrEmpty).collect(Collectors.toList());
	}

	public void setDescription(List<String> description) {
		this.description = new ArrayList<>() {{
			for (String line : description)
				if (!isNullOrEmpty(stripColor(line).replace(StringUtils.getColorChar(), "")))
					add(line.startsWith("&") ? line : "&f" + line);
		}};
	}

	public boolean isMarket() {
		return uuid.equals(UUID0);
	}

	public List<String> getDescriptionArray() {
		return description.isEmpty() ? List.of("", "", "", "") : description.stream().map(StringUtils::decolorize).toList();
	}

	public List<Product> getInStock(ShopGroup shopGroup) {
		return getProducts(shopGroup).stream().filter(product -> product.isEnabled() && product.isPurchasable() && product.canFulfillPurchase()).collect(Collectors.toList());
	}

	public List<Product> getOutOfStock(ShopGroup shopGroup) {
		return getProducts(shopGroup).stream().filter(product -> product.isEnabled() && product.isPurchasable() && !product.canFulfillPurchase()).collect(Collectors.toList());
	}

	public void addHolding(ShopGroup shopGroup, List<ItemStack> itemStacks) {
		if (isMarket())
			return;

		itemStacks.forEach(itemStack -> addHolding(shopGroup, itemStack));
	}

	public void addHolding(ShopGroup shopGroup, ItemStack itemStack) {
		if (isMarket())
			return;

		ItemUtils.combine(getHolding(shopGroup), itemStack.clone());
	}

	@NotNull
	public List<ItemStack> getHolding(ShopGroup shopGroup) {
		return holding.computeIfAbsent(shopGroup, $ -> new ArrayList<>());
	}

	public void removeProduct(Product product) {
		products.remove(product);
		ShopUtils.giveItems(uuid, product.getShopGroup(), product.getItemStacks());
	}

	public enum ShopGroup {
		SURVIVAL,
		SKYBLOCK,
		ONEBLOCK;

		public static ShopGroup of(org.bukkit.entity.Entity entity) {
			return of(entity.getWorld());
		}

		public static ShopGroup of(World world) {
			return of(world.getName());
		}

		public static ShopGroup of(String world) {
			try {
				return of(WorldGroup.of(world));
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}

		public static ShopGroup of(WorldGroup worldGroup) {
			try {
				return valueOf(worldGroup.name());
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}

		public static ShopGroup of(org.bukkit.entity.Entity entity, ShopGroup defaultValue) {
			return of(entity.getWorld(), defaultValue);
		}

		public static ShopGroup of(World world, ShopGroup defaultValue) {
			return of(world.getName(), defaultValue);
		}

		public static ShopGroup of(String world, ShopGroup defaultValue) {
			final ShopGroup result = of(world);
			return result == null ? defaultValue : result;
		}
	}

	public static void log(UUID seller, UUID customer, ShopGroup shopGroup, String item, int amount, ExchangeType exchangeType, String price, String priceAmount) {
		IOUtils.csvAppend("exchange", String.join(",", List.of(
			DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
			seller.toString(),
			Nickname.of(seller),
			customer.toString(),
			Nickname.of(customer),
			shopGroup.name(),
			item,
			String.valueOf(amount),
			exchangeType.name(),
			price,
			priceAmount
		)));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	@Converters({UUIDConverter.class, ItemStackConverter.class})
	public static class Product implements Comparable<Product> {
		@NonNull
		private UUID uuid;
		@NonNull
		private ShopGroup shopGroup;
		private boolean resourceWorld;
		private boolean purchasable = true;
		private boolean enabled = true;
		@Embedded
		private ItemStack item;
		private double stock;
		private ExchangeType exchangeType;
		private Object price;

		private transient boolean editing;

		public Product(@NonNull UUID uuid, @NonNull ShopGroup shopGroup, ItemStack item, double stock, ExchangeType exchangeType, Object price) {
			this(uuid, shopGroup, false, item, stock, exchangeType, price);
		}

		public Product(@NonNull UUID uuid, @NonNull ShopGroup shopGroup, boolean isResourceWorld, ItemStack item, double stock, ExchangeType exchangeType, Object price) {
			this.uuid = uuid;
			this.shopGroup = shopGroup;
			this.resourceWorld = isResourceWorld;
			this.item = item;
			this.stock = stock;
			this.exchangeType = exchangeType;
			this.price = price;
		}

		@PostLoad
		void fix(DBObject dbObject) {
			if (!(price instanceof Number))
				price = Json.deserializeItemStack((Map<String, Object>) dbObject.get("price"));
		}

		public Shop getShop() {
			return new ShopService().get(uuid);
		}

		public ItemStack getItem() {
			return item.clone();
		}

		public void addStock(int amount) {
			setStock(stock + amount);
		}

		public void removeStock(int amount) {
			setStock(stock - amount);
		}

		public void setStock(double stock) {
			if (isMarket())
				return;

			if (exchangeType == ExchangeType.BUY && stock < 0)
				this.stock = -1;
			else
				this.stock = Math.max(stock, 0);
		}

		public double getCalculatedStock() {
			if (exchangeType == ExchangeType.BUY && stock == -1)
				return new BankerService().getBalance(getShop(), shopGroup);
			else
				return stock;
		}

		public boolean canFulfillPurchase() {
			if (isMarket())
				return true;
			return getExchange().canFulfillPurchase();
		}

		private void validateProcess(Player customer) {
			if (uuid.equals(customer.getUniqueId()))
				throw new InvalidInputException("You cannot buy items from yourself");

			if (editing)
				throw new InvalidInputException("You cannot buy this item right now, it is being edited by the shop owner");
		}

		public void process(Player customer) {
			validateProcess(customer);
			getExchange().process(customer);
			log(customer);
		}

		public void processMany(Player customer, int times) {
			validateProcess(customer);
			getExchange().processMany(customer, times);
			log(customer, times);
		}

		public void processAll(Player customer) {
			validateProcess(customer);
			int count = getExchange().processAll(customer);
			log(customer, count);
		}

		public void log(Player customer) {
			log(customer, 1);
		}

		public void log(Player customer, int times) {
			final ShopService service = new ShopService();
			service.queueSave(5, service.get(customer));
			service.queueSave(5, getShop());

			for (int i = 0; i < times; i++) {
				String price;
				String priceAmount;

				if (this.price instanceof ItemStack) {
					price = ((ItemStack) this.price).getType().name();
					priceAmount = String.valueOf(((ItemStack) this.price).getAmount());
				} else {
					price = String.valueOf(this.price);
					priceAmount = "";
				}

				Shop.log(uuid, customer.getUniqueId(), shopGroup, stripColor(pretty(item).split(" ", 2)[1]), item.getAmount(), exchangeType, price, priceAmount);
			}
		}

		@NotNull
		public Exchange getExchange() {
			return exchangeType.init(this);
		}

		public ItemBuilder getItemWithLore() {
			ItemBuilder builder = new ItemBuilder(item).lore("&f");

			if (item.getType() != Material.ENCHANTED_BOOK)
				builder.itemFlags(ItemFlag.HIDE_ATTRIBUTES);

			short maxDurability = item.getType().getMaxDurability();
			if (item.getType() == Material.ENCHANTED_BOOK || maxDurability > 0)
				builder.lore("&7Repair Cost: " + ((Repairable) item.getItemMeta()).getRepairCost());

			if (item.getItemMeta() instanceof Damageable meta) {
				if (meta.hasDamage())
					builder.lore("&7Durability: " + (maxDurability - meta.getDamage()) + " / " + maxDurability);
			}

			if (item.getItemMeta() instanceof AxolotlBucketMeta meta) {
				Axolotl.Variant variant = Variant.LUCY;
				if (meta.hasVariant())
					variant = meta.getVariant();

				builder.modelId(variant.ordinal());
				builder.lore("&7Axolotl Type: " + camelCase(variant));
			}

			if (!getShulkerContents(item).isEmpty())
				builder.lore("&7Right click to view contents");

			if (item.getLore() != null) {
				if (builder.getLore().size() > (item.getLore().size() + 1))
					builder.lore("&f");
			} else if (builder.getLore().size() > 1)
				builder.lore("&f");

			return builder;
		}

		public ItemBuilder getItemWithCustomerLore() {
			if (!purchasable)
				return new ItemBuilder(item).lore("&f").lore("&cNot Purchasable");

			return getItemWithLore()
				.lore(getExchange().getLore())
				.lore("")
				.lore("&7Left click to " + getExchange().getCustomerAction().toLowerCase())
				.lore("&7Shift+Left click to " + getExchange().getCustomerAction().toLowerCase() + " many");
		}

		public ItemBuilder getItemWithOwnLore() {
			ItemBuilder builder;
			if (!purchasable)
				builder = new ItemBuilder(item).lore("&f").lore("&cNot Purchasable");
			else
				builder = getItemWithLore();

			if (!enabled)
				builder.lore("&cDisabled");

			if (purchasable)
				builder.lore(getExchange().getOwnLore());

			return builder.lore("", "&7Click to edit");
		}

		public boolean isMarket() {
			return getShop().isMarket();
		}

		public List<ItemStack> getItemStacks() {
			return getItemStacks(-1);
		}

		public List<ItemStack> getItemStacks(int maxStacks) {
			List<ItemStack> items = new ArrayList<>();

			if (exchangeType == ExchangeType.BUY)
				return items;

			ItemStack item = this.item.clone();
			double stock = this.stock;
			int maxStackSize = item.getMaxStackSize();

			while (stock > 0) {
				if (maxStacks > 0 && items.size() >= maxStacks)
					break;

				ItemStack next = new ItemStack(item.clone());
				next.setAmount((int) Math.min(maxStackSize, stock));
				stock -= next.getAmount();
				items.add(next);
			}

			return items;
		}

		public @Nullable Double getPricePerItem() {
			if (!(price instanceof Number)) return null;
			return (Double) price / item.getAmount();
		}

		@Override
		public int compareTo(@NotNull Product other) {
			// compare item type (ascending/alphabetical)
			int cmp = item.getType().name().compareTo(other.getItem().getType().name());
			if (cmp != 0) return cmp;

			// compare exchange type (ascending)
			cmp = exchangeType.compareTo(other.getExchangeType());
			if (cmp != 0) return cmp;

			// compare price (descending for BUY, ascending for SELL)
			if (getPricePerItem() != null && other.getPricePerItem() != null) {
				cmp = other.getPricePerItem().compareTo(getPricePerItem());
				if (cmp != 0) {
					cmp *= exchangeType == ExchangeType.BUY ? 1 : -1;
					return cmp;
				}
			} else if (getPricePerItem() != null) {
				cmp = getPricePerItem().compareTo(Double.MAX_VALUE);
				if (cmp != 0) return cmp;
			} else if (other.getPricePerItem() != null) {
				cmp = ((Double) Double.MAX_VALUE).compareTo(other.getPricePerItem());
				if (cmp != 0) return cmp;
			}

			return Integer.compare(item.getAmount(), other.getItem().getAmount());
		}
	}

	// Dumb enum due to morphia refusing to deserialize interfaces properly
	public enum ExchangeType implements IterableEnum {
		SELL(SellExchange.class),
		TRADE(TradeExchange.class),
		BUY(BuyExchange.class);

		@Getter
		private final Class<? extends Exchange> clazz;

		ExchangeType(Class<? extends Exchange> clazz) {
			this.clazz = clazz;
		}

		@SneakyThrows
		public Exchange init(Product product) {
			return (Exchange) clazz.getDeclaredConstructors()[0].newInstance(product);
		}
	}

	public interface Exchange {

		Product getProduct();
		Object getPrice();

		void validateProcessOne(Player customer);
		void validateProcessMany(Player customer);

		void processOne(Player customer);

		default void process(Player customer) {
			processOne(customer);
			PlayerUtils.send(customer, PREFIX + explainPurchase());
		}

		default int processAll(Player customer) {
			int count = 0;
			while (true) {
				try {
					validateProcessMany(customer);
					processOne(customer);
					++count;
				} catch (InvalidInputException ex) {
					if (count == 0)
						throw ex;
					break;
				}
			}

			PlayerUtils.send(customer, PREFIX + explainPurchase(count));
			return count;
		}

		default int processMany(Player customer, int times) {
			int count = 0;

			for (int i = 0; i < times; i++) {
				try {
					validateProcessMany(customer);
					processOne(customer);
					++count;
				} catch (InvalidInputException ex) {
					if (count == 0)
						throw ex;
					break;
				}
			}

			PlayerUtils.send(customer, PREFIX + explainPurchase(count));
			return count;
		}

		default String prettyPrice() {
			return prettyPrice(1);
		}

		default String explainPurchase() {
			return explainPurchase(1);
		}

		String prettyPrice(int count);
		String explainPurchase(int count);

		boolean canFulfillPurchase();

		String getCustomerAction();

		List<String> getLore();
		List<String> getOwnLore();

		default String stockColor() {
			return canFulfillPurchase() ? "&e" : "&c";
		}

		default void checkStock() {
			if (!getProduct().isMarket()) {
				if (getProduct().getCalculatedStock() <= 0)
					throw new InvalidInputException("This item is out of stock");
				if (!canFulfillPurchase())
					throw new InvalidInputException("There is not enough stock to fulfill your purchase");
			}
		}
	}

	@Data
	// Customer buying an item from the shop owner for money (most common/default)
	public static class SellExchange implements Exchange {
		@NonNull
		private final Product product;
		private final Double price;

		public SellExchange(@NonNull Product product) {
			this.product = product;
			this.price = (double) product.getPrice();
		}

		@Override
		public void validateProcessOne(Player customer) {
			checkStock();

			if (!Banker.of(customer).has(price, product.getShopGroup()))
				throw new InvalidInputException("You do not have enough money to purchase this item");
		}

		@Override
		public void validateProcessMany(Player customer) {
			if (!hasRoomFor(customer, getProduct().getItem()))
				throw new InvalidInputException("You do not have enough inventory space for " + pretty(getProduct().getItem()));
		}

		@Override
		public void processOne(Player customer) {
			validateProcessOne(customer);

			product.setStock(product.getStock() - product.getItem().getAmount());
			transaction(customer);
			giveItems(customer.getUniqueId(), product.getShopGroup(), product.getItem());
		}

		@Override
		public String prettyPrice(int count) {
			return prettyMoney(price * count);
		}

		@Override
		public String explainPurchase(int count) {
			return "You purchased &e" + pretty(product.getItem(), count) + " &3for &e" + prettyPrice(count);
		}

		private void transaction(Player customer) {
			if (price <= 0)
				return;

			TransactionCause cause = product.isMarket() ? TransactionCause.MARKET_SALE : TransactionCause.SHOP_SALE;
			Transaction transaction = cause.of(customer, product.getShop(), BigDecimal.valueOf(price), product.getShopGroup(), pretty(product.getItem()));
			new BankerService().transfer(customer, product.getShop(), BigDecimal.valueOf(price), product.getShopGroup(), transaction);
		}

		public boolean canFulfillPurchase() {
			return product.getCalculatedStock() >= product.getItem().getAmount();
		}

		@Override
		public String getCustomerAction() {
			return "Purchase";
		}

		@Override
		public List<String> getLore() {
			int stock = (int) product.getStock();
			String desc = "&7Buy &e" + product.getItem().getAmount() + " &7for &a" + prettyPrice();

			if (product.isMarket())
				return Arrays.asList(
					desc,
					"&7Owner: &6Market"
				);
			else
				return Arrays.asList(
					desc,
					"&7Stock: " + stockColor() + stock,
					"&7Owner: &e" + Nickname.of(product.getShop())
				);
		}

		@Override
		public List<String> getOwnLore() {
			int stock = (int) product.getStock();
			return Arrays.asList(
				"&7Selling &e" + product.getItem().getAmount() + " &7for &a" + prettyPrice(),
				"&7Stock: " + stockColor() + stock
			);
		}
	}

	@Data
	// Customer buying an item from the shop owner for other items
	public static class TradeExchange implements Exchange {
		@NonNull
		private Product product;
		private final ItemStack price;

		public TradeExchange(@NonNull Product product) {
			this.product = product;
			this.price = (ItemStack) product.getPrice();
		}

		@Override
		public void validateProcessOne(Player customer) {
			checkStock();

			if (!customer.getInventory().containsAtLeast(price, price.getAmount()))
				throw new InvalidInputException("You do not have " + prettyPrice() + " to purchase this item");
		}

		@Override
		public void validateProcessMany(Player customer) {
			if (!hasRoomFor(customer, getProduct().getItem()))
				throw new InvalidInputException("You do not have enough inventory space for " + pretty(getProduct().getItem()));
		}

		@Override
		public void processOne(Player customer) {
			validateProcessOne(customer);

			product.setStock(product.getStock() - product.getItem().getAmount());
			customer.getInventory().removeItem(price);
			product.getShop().addHolding(product.getShopGroup(), price);
			giveItems(customer.getUniqueId(), product.getShopGroup(), product.getItem());
		}

		@Override
		public String prettyPrice(int count) {
			return pretty(price, count);
		}

		@Override
		public String explainPurchase(int count) {
			return "You purchased &e" + pretty(product.getItem(), count) + " &3for &e" + prettyPrice(count);
		}

		@Override
		public boolean canFulfillPurchase() {
			return product.getCalculatedStock() >= product.getItem().getAmount();
		}

		@Override
		public String getCustomerAction() {
			return "Purchase";
		}

		@Override
		public List<String> getLore() {
			int stock = (int) product.getStock();
			String desc = "&7Buy &e" + product.getItem().getAmount() + " &7for &a" + prettyPrice();
			if (product.isMarket())
				return Arrays.asList(
					desc,
					"&7Owner: &6Market"
				);
			else
				return Arrays.asList(
					desc,
					"&7Stock: " + stockColor() + stock,
					"&7Owner: &e" + Nickname.of(product.getShop())
				);
		}

		@Override
		public List<String> getOwnLore() {
			int stock = (int) product.getStock();
			return Arrays.asList(
				"&7Selling &e" + product.getItem().getAmount() + " &7for &a" + prettyPrice(),
				"&7Stock: " + stockColor() + stock
			);
		}
	}

	@Data
	// Customer selling an item to the shop owner for money
	public static class BuyExchange implements Exchange {
		@NonNull
		private final Product product;
		private final Double price;

		public BuyExchange(@NonNull Product product) {
			this.product = product;
			this.price = (Double) product.getPrice();
		}

		@Override
		public void validateProcessOne(Player customer) {
			checkStock();

			if (isNullOrEmpty(getMatchingItems(customer)))
				throw new InvalidInputException("You do not have " + pretty(product.getItem()) + " to sell");
		}

		@Override
		public void validateProcessMany(Player customer) {
		}

		@Override
		public void processOne(Player customer) {
			validateProcessOne(customer);

			product.setStock(product.getStock() - price);
			transaction(customer);

			for (ItemStack item : getMatchingItems(customer)) {
				customer.getInventory().removeItem(item);
				product.getShop().addHolding(product.getShopGroup(), item);
			}
		}

		private List<ItemStack> getMatchingItems(Player customer) {
			List<ItemStack> found = new ArrayList<>();
			final int needed = product.getItem().getAmount();
			Supplier<Integer> count = () -> found.stream().mapToInt(ItemStack::getAmount).sum();
			Supplier<Integer> left = () -> needed - count.get();
			for (ItemStack item : customer.getInventory()) {
				if (isNullOrAir(item))
					continue;

				ItemStack cloned = item.clone();
				ItemTagsUtils.clearTags(cloned);

				if (!cloned.isSimilar(product.getItem()))
					continue;

				found.add(ItemUtils.clone(item, item.getAmount() <= left.get() ? item.getAmount() : left.get()));

				if (left.get() < 1)
					break;
			}

			if (count.get() != needed)
				return Collections.emptyList();

			return found;
		}

		public BigDecimal processResourceMarket(Player customer, ItemStack item) {
			if (product.getItem().getAmount() != 1)
				throw new InvalidInputException("Resource market product amount must be 1 (" + pretty(product.getItem()) + ")");

			BigDecimal profit = new BigDecimal(0);
			for (int i = 0; i < item.getAmount(); i++) {
				transaction(customer);
				profit = profit.add(BigDecimal.valueOf(price));
			}

			product.log(customer, item.getAmount());

			return profit;
		}

		@Override
		public String prettyPrice(int count) {
			return prettyMoney(price * count);
		}

		@Override
		public String explainPurchase(int count) {
			return "You sold &e" + pretty(product.getItem(), count) + " &3for &e" + prettyPrice(count);
		}

		private void transaction(Player customer) {
			if (price <= 0)
				return;
			TransactionCause cause = product.isMarket() ? TransactionCause.MARKET_PURCHASE : TransactionCause.SHOP_PURCHASE;
			Transaction transaction = cause.of(product.getShop(), customer, BigDecimal.valueOf(price), product.getShopGroup(), pretty(product.getItem()));
			new BankerService().transfer(product.getShop(), customer, BigDecimal.valueOf(price), product.getShopGroup(), transaction);
		}

		@Override
		public boolean canFulfillPurchase() {
			return product.getCalculatedStock() >= price && new BankerService().get(product.getUuid()).has(price, product.getShopGroup());
		}

		@Override
		public String getCustomerAction() {
			return "Sell";
		}

		@Override
		public List<String> getLore() {
			String desc = "&7Sell &e" + product.getItem().getAmount() + " &7for &a" + prettyPrice();
			if (product.isMarket())
				return Arrays.asList(
					desc,
					"&7Owner: &6Market"
				);
			else
				return Arrays.asList(
					desc,
					"&7Stock: " + stockColor() + prettyMoney(product.getCalculatedStock(), false),
					"&7Owner: &e" + Nickname.of(product.getShop())
				);
		}

		@Override
		public List<String> getOwnLore() {
			return Arrays.asList(
				"&7Buying &e" + product.getItem().getAmount() + " &7for &a" + prettyPrice(),
				"&7Stock: " + stockColor() + prettyMoney(product.getCalculatedStock(), false)
			);
		}

	}

}
