package me.pugabyte.bncore.features.shops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.shops.providers.BrowseItemsProvider;
import me.pugabyte.bncore.features.shops.providers.BrowseMarketProvider;
import me.pugabyte.bncore.features.shops.providers.BrowseShopsProvider;
import me.pugabyte.bncore.features.shops.providers.MainMenuProvider;
import me.pugabyte.bncore.features.shops.providers.PlayerShopProvider;
import me.pugabyte.bncore.features.shops.providers.SearchItemsProvider;
import me.pugabyte.bncore.features.shops.providers.YourShopProvider;
import me.pugabyte.bncore.features.shops.providers._ShopProvider;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.shop.Shop.ExchangeType;
import me.pugabyte.bncore.models.shop.Shop.Product;
import me.pugabyte.bncore.utils.Utils.IteratableEnum;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public enum ShopMenu {
	MAIN_MENU(MainMenuProvider.class),
	BROWSE_MARKET(BrowseMarketProvider.class),
	BROWSE_SHOPS(BrowseShopsProvider.class),
	BROWSE_ITEMS(BrowseItemsProvider.class),
	SEARCH_ITEMS(SearchItemsProvider.class),
	YOUR_SHOP(YourShopProvider.class),

	PLAYER_SHOP(PlayerShopProvider.class);

	private Class<? extends _ShopProvider> provider;

	ShopMenu(Class<? extends _ShopProvider> provider) {
		this.provider = provider;
	}

	@SneakyThrows
	public void open(Player viewer, _ShopProvider previousMenu, Object... objects) {
		List<Object> args = new java.util.ArrayList<>(Collections.singletonList(previousMenu));
		args.addAll(Arrays.asList(objects));

		for (Constructor<?> constructor : this.provider.getDeclaredConstructors())
			if (constructor.getParameterCount() == args.size()) {
				_ShopProvider provider = (_ShopProvider) constructor.newInstance(args.toArray(new Object[0]));
				provider.open(viewer);
				return;
			}
		throw new InvalidInputException("Could not find a constructor that matched the provided length of arguments (" + args.size() + ")");
	}

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
