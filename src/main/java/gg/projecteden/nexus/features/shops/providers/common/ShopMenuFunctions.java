package gg.projecteden.nexus.features.shops.providers.common;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.shops.providers.SearchProductsProvider;
import gg.projecteden.nexus.models.shop.Shop.ExchangeType;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.LanguageUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Comparator;
import java.util.function.Predicate;

public class ShopMenuFunctions {

	@Data
	@AllArgsConstructor
	public static class Filter {
		private FilterType type;
		private Predicate<Product> filter;
		private String message;
	}

	public interface FilterType extends IterableEnum {

		String name();

		default Predicate<Product> getFilter() {
			return null;
		}

		default Filter get() {
			return new Filter(this, getFilter(), null);
		}

		default Filter of(String message) {
			return new Filter(this, getFilter(), message);
		}

		default Filter of(Predicate<Product> filter) {
			return new Filter(this, filter, null);
		}

		default Filter of(String message, Predicate<Product> filter) {
			return new Filter(this, filter, message);
		}
	}

	public enum FilterRequiredType implements FilterType {
		REQUIRED
	}

	public enum FilterSearchType implements FilterType {
		SEARCH;

		public Filter of(String message) {
			return SEARCH.of(message, product -> SearchProductsProvider.filter(product.getItem(), item -> matches(item, message)));
		}

		public boolean matches(ItemStack item, String filter) {
			return indexOf(item, filter) != -1;
		}

		public int indexOf(ItemStack item, String filter) {
			String input = filter.trim();
			Material type = item.getType();

			int index = indexOf(type.name(), input);
			if (index != -1)
				return index;

			index = indexOf(LanguageUtils.translate(type), input);
			if (index != -1)
				return index;

			if (item.getItemMeta().hasDisplayName()) {
				index = indexOf(StringUtils.stripColor(item.getItemMeta().getDisplayName()), input);
				if (index != -1)
					return index;
			}

			for (Enchantment enchantment : item.getEnchantments().keySet()) {
				index = indexOf(enchantment.getKey().getKey(), input);
				if (index != -1)
					return index;
			}

			if (item.getItemMeta() instanceof EnchantmentStorageMeta meta)
				for (Enchantment enchantment : meta.getStoredEnchants().keySet()) {
					index = indexOf(enchantment.getKey().getKey(), input);
					if (index != -1)
						return index;
				}

			if (item.getItemMeta() instanceof PotionMeta meta) {
				final PotionType effectType = meta.getBasePotionType();
				if (effectType != null) {
					index = indexOf(ItemUtils.getFixedPotionName(effectType), input);
					if (index != -1)
						return index;
				}

				for (PotionEffect effect : meta.getCustomEffects()) {
					index = indexOf(ItemUtils.getFixedPotionName(effect.getType()), input);
					if (index != -1)
						return index;
				}
			}

			return -1;
		}

		private int indexOf(String key, String message) {
			var index = key.toLowerCase().indexOf(message.toLowerCase());
			if (index != -1)
				return index;

			return key.replace("_", " ").toLowerCase().indexOf(message.toLowerCase());
		}
	}

	public enum FilterExchangeType implements FilterType {
		BOTH,
		BUYING(product -> product.getExchangeType() == ExchangeType.TRADE || product.getExchangeType() == ExchangeType.SELL),
		SELLING(product -> product.getExchangeType() == ExchangeType.BUY);

		@Getter
		private Predicate<Product> filter;

		FilterExchangeType() {}

		FilterExchangeType(Predicate<Product> filter) {
			this.filter = filter;
		}
	}

	public enum FilterMarketItems implements FilterType {
		SHOWN,
		HIDDEN(product -> !product.isMarket());

		@Getter
		private Predicate<Product> filter;

		FilterMarketItems() {}

		FilterMarketItems(Predicate<Product> filter) {
			this.filter = filter;
		}
	}

	public enum FilterEmptyStock implements FilterType {
		SHOWN,
		HIDDEN(product -> !product.isPurchasable() || product.canFulfillPurchase());

		@Getter
		private Predicate<Product> filter;

		FilterEmptyStock() {}

		FilterEmptyStock(Predicate<Product> filter) {
			this.filter = filter;
		}
	}

	@Data
	@AllArgsConstructor
	public static class Sorter {
		private SorterType type;
		private Comparator<Product> comparator;
	}

	public interface SorterType extends IterableEnum {

		String name();

		default Comparator<Product> getComparator() {
			return null;
		}

		default Sorter get() {
			return new Sorter(this, getComparator());
		}
	}

	public enum BestMatchSorter implements SorterType {
		BEST_MATCH,
		;

		public Sorter of(String query) {
			return new Sorter(this, Comparator
				.<Product>comparingInt(product -> FilterSearchType.SEARCH.indexOf(product.getItem(), query))
				.thenComparing(Product::compareTo));
		}
	}

	@Getter
	@AllArgsConstructor
	public enum DefaultSorterType implements SorterType {
		ALPHABETICAL(Product::compareTo),

		PRICE_LOW_TO_HIGH((product, other) -> {
			Object price1 = product.getPrice();
			Object price2 = other.getPrice();
			if (price1 instanceof Number number1 && price2 instanceof Number number2) {
				var price = Double.compare(number1.doubleValue(), number2.doubleValue());
				if (price != 0) return price;
			}
			return product.compareTo(other);
		}),

		PRICE_HIGH_TO_LOW((product, other) -> {
			Object price1 = product.getPrice();
			Object price2 = other.getPrice();
			if (price1 instanceof Number number1 && price2 instanceof Number number2) {
				var price = Double.compare(number1.doubleValue(), number2.doubleValue());
				if (price != 0) return -price;
			}
			return product.compareTo(other);
		}),

		PRICE_PER_ITEM_LOW_TO_HIGH((product, other) -> {
			Double price1 = product.getPricePerItem();
			Double price2 = other.getPricePerItem();
			if (price1 != null && price2 != null) {
				var price = Double.compare(price1, price2);
				if (price != 0) return price;
			}
			return product.compareTo(other);
		}),

		PRICE_PER_ITEM_HIGH_TO_LOW((product, other) -> {
			Double price1 = product.getPricePerItem();
			Double price2 = other.getPricePerItem();
			if (price1 != null && price2 != null) {
				var price = Double.compare(price1, price2);
				if (price != 0) return -price;
			}
			return product.compareTo(other);
		}),

		TOTAL_PRICE_LOW_TO_HIGH((product, other) -> {
			Double price1 = product.getTotalPrice();
			Double price2 = other.getTotalPrice();
			if (price1 != null && price2 != null) {
				var price = Double.compare(price1, price2);
				if (price != 0) return price;
			}
			return product.compareTo(other);
		}),

		TOTAL_PRICE_HIGH_TO_LOW((product, other) -> {
			Double price1 = product.getTotalPrice();
			Double price2 = other.getTotalPrice();
			if (price1 != null && price2 != null) {
				var price = Double.compare(price1, price2);
				if (price != 0) return -price;
			}
			return product.compareTo(other);
		}),

		STOCK_LOW_TO_HIGH(Comparator.comparingDouble(Product::getStock)
			.thenComparing(product -> product)),

		STOCK_HIGH_TO_LOW(Comparator.comparingDouble((Product product) -> -product.getStock())
			.thenComparing(product -> product)),
		;

		private final Comparator<Product> comparator;
	}

}
