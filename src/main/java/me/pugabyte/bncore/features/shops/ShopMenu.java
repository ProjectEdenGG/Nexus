package me.pugabyte.bncore.features.shops;

import lombok.SneakyThrows;
import me.pugabyte.bncore.features.shops.providers.BrowseItemsProvider;
import me.pugabyte.bncore.features.shops.providers.BrowseMarketProvider;
import me.pugabyte.bncore.features.shops.providers.BrowseShopsProvider;
import me.pugabyte.bncore.features.shops.providers.MainMenuProvider;
import me.pugabyte.bncore.features.shops.providers.PlayerShopProvider;
import me.pugabyte.bncore.features.shops.providers.ViewCategoriesProvider;
import me.pugabyte.bncore.features.shops.providers.YourShopProvider;
import me.pugabyte.bncore.features.shops.providers._ShopProvider;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ShopMenu {
	MAIN_MENU(MainMenuProvider.class),
	BROWSE_MARKET(BrowseMarketProvider.class),
	BROWSE_SHOPS(BrowseShopsProvider.class),
	BROWSE_ITEMS(BrowseItemsProvider.class),
	VIEW_CATEGORIES(ViewCategoriesProvider.class),
	YOUR_SHOP(YourShopProvider.class),

	PLAYER_SHOP(PlayerShopProvider.class);

	private Class<? extends _ShopProvider> provider;

	ShopMenu(Class<? extends _ShopProvider> provider) {
		this.provider = provider;
	}

	@SneakyThrows
	public void open(Player viewer, _ShopProvider previousMenu, Object... objects) {
		List<Object> o = new java.util.ArrayList<>(Collections.singletonList(previousMenu));
		o.addAll(Arrays.asList(objects));

		_ShopProvider provider = (_ShopProvider) this.provider.getDeclaredConstructors()[0].newInstance(o.toArray(new Object[0]));
		provider.open(viewer);
	}
}
