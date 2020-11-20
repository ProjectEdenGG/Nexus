package me.pugabyte.nexus.features.shops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.shop.Shop.ExchangeType;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.utils.Utils.IteratableEnum;

import java.util.function.Predicate;

public class ShopMenuFunctions {

	@Data
	@AllArgsConstructor
	public static class Filter {
		private FilterType type;
		private Predicate<Product> filter;
		private String message;
	}

	public interface FilterType extends IteratableEnum {

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

	public enum FilterSearchType implements FilterType {
		SEARCH
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
		HIDDEN(product -> product.getShop().getUuid() != Nexus.getUUID0());

		@Getter
		private Predicate<Product> filter;

		FilterMarketItems() {}

		FilterMarketItems(Predicate<Product> filter) {
			this.filter = filter;
		}
	}

	public enum FilterEmptyStock implements FilterType {
		SHOWN,
		HIDDEN(product -> product.getStock() != 0); // TODO check balance?

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
