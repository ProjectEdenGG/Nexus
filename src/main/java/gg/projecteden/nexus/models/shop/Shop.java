package gg.projecteden.nexus.models.shop;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.shops.ShopUtils;
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
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.EnumUtils.IteratableEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Axolotl.Variant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static gg.projecteden.nexus.features.shops.ShopUtils.giveItems;
import static gg.projecteden.nexus.features.shops.ShopUtils.prettyMoney;
import static gg.projecteden.nexus.features.shops.Shops.PREFIX;
import static gg.projecteden.nexus.utils.ItemUtils.getShulkerContents;
import static gg.projecteden.nexus.utils.PlayerUtils.hasRoomFor;
import static gg.projecteden.nexus.utils.StringUtils.pretty;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.utils.StringUtils.camelCase;

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
	private List<ItemStack> holding = new ArrayList<>();
	@Embedded
	private List<Material> disabledResourceMarketItems = new ArrayList<>();

	// TODO holding for money, maybe? would make withdrawing money more complicated
	// private double profit;

	public List<Product> getProducts(ShopGroup shopGroup) {
		return products.stream().filter(product -> product.getShopGroup().equals(shopGroup)).collect(Collectors.toList());
	}

	public List<String> getDescription() {
		return description.stream().filter(line -> !isNullOrEmpty(line)).collect(Collectors.toList());
	}

	public void setDescription(List<String> description) {
		this.description = new ArrayList<>() {{
			for (String line : description)
				if (!isNullOrEmpty(stripColor(line).replace(StringUtils.getColorChar(), "")))
					add(line.startsWith("&") ? line : "&f" + line);
		}};
	}

	public boolean isMarket() {
		return uuid.equals(StringUtils.getUUID0());
	}

	public String[] getDescriptionArray() {
		return description.isEmpty() ? new String[]{"", "", "", ""} : description.stream().map(StringUtils::decolorize).toArray(String[]::new);
	}

	public List<Product> getInStock(ShopGroup shopGroup) {
		return getProducts(shopGroup).stream().filter(product -> product.isEnabled() && product.isPurchasable() && product.canFulfillPurchase()).collect(Collectors.toList());
	}

	public List<Product> getOutOfStock(ShopGroup shopGroup) {
		return getProducts(shopGroup).stream().filter(product -> product.isEnabled() && product.isPurchasable() && !product.canFulfillPurchase()).collect(Collectors.toList());
	}

	public void addHolding(List<ItemStack> itemStacks) {
		if (isMarket())
			return;

		itemStacks.forEach(this::addHolding);
	}

	public void addHolding(ItemStack itemStack) {
		if (isMarket())
			return;

		ItemUtils.combine(holding, itemStack.clone());
	}

	public void removeProduct(Product product) {
		products.remove(product);
		ShopUtils.giveItems(uuid, product.getItemStacks());
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
				return valueOf(WorldGroup.of(world).name());
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
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

		public void processAll(Player customer) {
			validateProcess(customer);
			int count = getExchange().processAll(customer);
			log(customer, count);
		}

		public void log(Player customer) {
			log(customer, 1);
		}

		public void log(Player customer, int times) {
			for (int i = 0; i < times; i++) {
				List<String> columns = new ArrayList<>(Arrays.asList(
					DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
					getUuid().toString(),
					getShop().getName(),
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

				IOUtils.csvAppend("exchange", String.join(",", columns));
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

			if (item.getItemMeta() instanceof BlockStateMeta meta) {
				if (meta.getBlockState() instanceof Beehive beehive)
					builder.lore("&7Bees: " + beehive.getEntityCount() + " / " + beehive.getMaxEntities());
			}

			if (item.getItemMeta() instanceof AxolotlBucketMeta meta) {
				Axolotl.Variant variant = Variant.LUCY;
				if (meta.hasVariant())
					variant = meta.getVariant();

				builder.customModelData(variant.ordinal());
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

			ItemBuilder builder = getItemWithLore().lore(getExchange().getLore());

			builder.lore("")
				.lore("&7Left click to " + getExchange().getCustomerAction().toLowerCase())
				.lore("&7Shift+Left click to " + getExchange().getCustomerAction().toLowerCase() + " all");

			return builder;
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

		@Override
		public int compareTo(@NotNull Product product) {
			if (item.getType().name().equals(product.getItem().getType().name())) {
				if (exchangeType != product.getExchangeType())
					return exchangeType.compareTo(product.getExchangeType());
				else if (price instanceof Number && product.getPrice() instanceof Number)
					if (exchangeType == ExchangeType.BUY)
						return ((Double) product.getPrice()).compareTo((Double) price);
					else
						return ((Double) price).compareTo((Double) product.getPrice());
				else if (price instanceof Number)
					return ((Double) price).compareTo(Double.MAX_VALUE);
				else if (product.getPrice() instanceof Number)
					return ((Double) Double.MAX_VALUE).compareTo(((Double) product.getPrice()));
				else
					return 0;
			} else
				return item.getType().name().compareTo(product.getItem().getType().name());
		}
	}

	// Dumb enum due to morphia refusing to deserialize interfaces properly
	public enum ExchangeType implements IteratableEnum {
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
		void validateProcessAll(Player customer);

		void processOne(Player customer);

		default void process(Player customer) {
			processOne(customer);
			new ShopService().save(getProduct().getShop());
			PlayerUtils.send(customer, PREFIX + explainPurchase());
		}

		default int processAll(Player customer) {
			int count = 0;
			while (true) {
				try {
					validateProcessAll(customer);
					processOne(customer);
					++count;
				} catch (InvalidInputException ex) {
					if (count == 0)
						throw ex;
					break;
				}
			}

			new ShopService().queueSave(5, getProduct().getShop());
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
	// Customer buying an item from the shop owner for money
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
		public void validateProcessAll(Player customer) {
			if (!hasRoomFor(customer, getProduct().getItem()))
				throw new InvalidInputException("You do not have enough inventory space for " + pretty(getProduct().getItem()));
		}

		@Override
		public void processOne(Player customer) {
			validateProcessOne(customer);

			product.setStock(product.getStock() - product.getItem().getAmount());
			transaction(customer);
			giveItems(customer.getUniqueId(), product.getItem());
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
		public void validateProcessAll(Player customer) {
			if (!hasRoomFor(customer, getProduct().getItem()))
				throw new InvalidInputException("You do not have enough inventory space for " + pretty(getProduct().getItem()));
		}

		@Override
		public void processOne(Player customer) {
			validateProcessOne(customer);

			product.setStock(product.getStock() - product.getItem().getAmount());
			customer.getInventory().removeItem(price);
			product.getShop().addHolding(price);
			giveItems(customer.getUniqueId(), product.getItem());
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

			// TODO: Allow items with itemtags to be sold on the market
//			if(ItemTagsUtils.isTagable(product.getItem()))

			if (!customer.getInventory().containsAtLeast(product.getItem(), product.getItem().getAmount()))
				throw new InvalidInputException("You do not have " + pretty(product.getItem()) + " to sell");
		}

		@Override
		public void validateProcessAll(Player customer) {
		}

		@Override
		public void processOne(Player customer) {
			validateProcessOne(customer);

			product.setStock(product.getStock() - price);
			transaction(customer);

			// TODO: Allow items with itemtags to be sold on the market
			customer.getInventory().removeItem(product.getItem());

			product.getShop().addHolding(product.getItem());
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
