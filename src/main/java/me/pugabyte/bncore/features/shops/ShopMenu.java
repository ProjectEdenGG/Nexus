package me.pugabyte.bncore.features.shops;

import lombok.SneakyThrows;
import me.pugabyte.bncore.features.shops.providers.BrowseItemsProvider;
import me.pugabyte.bncore.features.shops.providers.BrowseMarketProvider;
import me.pugabyte.bncore.features.shops.providers.BrowseShopsProvider;
import me.pugabyte.bncore.features.shops.providers.MainMenuProvider;
import me.pugabyte.bncore.features.shops.providers.PlayerShopProvider;
import me.pugabyte.bncore.features.shops.providers.SearchItemsProvider;
import me.pugabyte.bncore.features.shops.providers.ViewCategoriesProvider;
import me.pugabyte.bncore.features.shops.providers.YourShopProvider;
import me.pugabyte.bncore.features.shops.providers._ShopProvider;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ShopMenu {
	MAIN_MENU(MainMenuProvider.class),
	BROWSE_MARKET(BrowseMarketProvider.class),
	BROWSE_SHOPS(BrowseShopsProvider.class),
	BROWSE_ITEMS(BrowseItemsProvider.class),
	SEARCH_ITEMS(SearchItemsProvider.class),
	VIEW_CATEGORIES(ViewCategoriesProvider.class),
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
}
