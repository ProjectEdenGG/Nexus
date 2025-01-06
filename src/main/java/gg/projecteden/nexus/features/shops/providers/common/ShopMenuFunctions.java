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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
			return SEARCH.of(message, product -> SearchProductsProvider.filter(product.getItem(), item -> {
				String input = message.trim();
				Material type = item.getType();

				if (contains(type.name(), input))
					return true;

				if (contains(LanguageUtils.translate(type), input))
					return true;

				if (item.getItemMeta().hasDisplayName())
					if (contains(StringUtils.stripColor(item.getItemMeta().getDisplayName()), input))
						return true;

				for (Enchantment enchantment : item.getEnchantments().keySet())
					if (contains(enchantment.getKey().getKey(), input))
						return true;

				if (item.getItemMeta() instanceof EnchantmentStorageMeta meta)
					for (Enchantment enchantment : meta.getStoredEnchants().keySet())
						if (contains(enchantment.getKey().getKey(), input))
							return true;

				if (item.getItemMeta() instanceof PotionMeta meta) {
					final PotionEffectType effectType = meta.getBasePotionType().getEffectType();
					if (effectType != null)
						if (contains(ItemUtils.getFixedPotionName(effectType), input))
							return true;

					for (PotionEffect effect : meta.getCustomEffects())
						if (contains(ItemUtils.getFixedPotionName(effect.getType()), input))
							return true;
				}

				return false;
			}));
		}

		private boolean contains(String key, String message) {
			if (key.toLowerCase().contains(message.toLowerCase()))
				return true;
			if (key.replace("_", " ").toLowerCase().contains(message.toLowerCase()))
				return true;

			return false;
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

	public enum SortType {
		ALPHABETICAL,
		STOCK,
		PRICE
	}

}
