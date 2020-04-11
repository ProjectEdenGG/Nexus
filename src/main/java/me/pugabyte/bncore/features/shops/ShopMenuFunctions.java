package me.pugabyte.bncore.features.shops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.shop.Shop.ExchangeType;
import me.pugabyte.bncore.models.shop.Shop.Product;
import me.pugabyte.bncore.utils.Utils.IteratableEnum;

import java.util.function.Function;

public class ShopMenuFunctions {

	@Data
	@AllArgsConstructor
	public static class Filter {
		private FilterType type;
		private Function<Product, Boolean> filter;
		private String message;
	}

	public interface FilterType extends IteratableEnum {

		String name();

		default Function<Product, Boolean> getFilter() {
			return null;
		}

		default Filter get() {
			return new Filter(this, getFilter(), null);
		}

		default Filter of(String message) {
			return new Filter(this, getFilter(), message);
		}

		default Filter of(Function<Product, Boolean> filter) {
			return new Filter(this, filter, null);
		}

		default Filter of(String message, Function<Product, Boolean> filter) {
			return new Filter(this, filter, message);
		}
	}

	public enum FilterSearchType implements FilterType {
		SEARCH
	}

	public enum FilterExchangeType implements FilterType {
		BOTH,
		BUYING(product -> product.getExchangeType() == ExchangeType.ITEM_FOR_ITEM || product.getExchangeType() == ExchangeType.ITEM_FOR_MONEY),
		SELLING(product -> product.getExchangeType() == ExchangeType.MONEY_FOR_ITEM);

		@Getter
		private Function<Product, Boolean> filter;

		FilterExchangeType() {}

		FilterExchangeType(Function<Product, Boolean> filter) {
			this.filter = filter;
		}
	}

	public enum FilterMarketItems implements FilterType {
		SHOWN,
		HIDDEN(product -> product.getShop().getUuid() != BNCore.getUUID0());

		@Getter
		private Function<Product, Boolean> filter;

		FilterMarketItems() {}

		FilterMarketItems(Function<Product, Boolean> filter) {
			this.filter = filter;
		}
	}

	public enum FilterEmptyStock implements FilterType {
		SHOWN,
		HIDDEN(product -> product.getStock() != 0); // TODO check balance?

		@Getter
		private Function<Product, Boolean> filter;

		FilterEmptyStock() {}

		FilterEmptyStock(Function<Product, Boolean> filter) {
			this.filter = filter;
		}
	}

	public enum SortType {
		ALPHABETICAL,
		STOCK,
		PRICE
	}

}
