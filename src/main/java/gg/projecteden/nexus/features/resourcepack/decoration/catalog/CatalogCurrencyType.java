package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.TypeConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public enum CatalogCurrencyType {
	MONEY,
	TOKENS,
	;

	public Double getCatalogPrice(DecorationConfig config) {
		DecorationType type = DecorationType.of(config);
		if (type == null)
			return null;

		TypeConfig typeConfig = type.getTypeConfig();
		if (typeConfig == null || typeConfig.price() == -1)
			return null;

		if (this == TOKENS)
			return (double) typeConfig.tokens();

		return typeConfig.price();
	}

	public ItemStack getCatalogItem(Player viewer, DecorationConfig config) {
		Double price = getCatalogPrice(config);
		if (price == null)
			return null;

		if (DecorationUtils.hasBypass(viewer))
			price = 0d;

		return config.getItemBuilder().lore("", getPriceString(price)).build();
	}

	private String getPriceString(double price) {
		if (price == 0)
			return "free";

		return switch (this) {
			case MONEY -> "&3Price: &a" + DecorationUtils.prettyMoney(price);
			case TOKENS -> "&3Tokens: &a" + price;
		};
	}

	public boolean hasFunds(Player player, ShopGroup shopGroup, double price) {
		switch (this) {
			case MONEY -> {
				BankerService bankerService = new BankerService();
				return bankerService.has(player, price, shopGroup);
			}
			case TOKENS -> {
				EventUserService eventUserService = new EventUserService();
				return (eventUserService.get(player).getTokens()) >= price;
			}

		}

		return false;
	}

	public void withdraw(Player player, ShopGroup shopGroup, DecorationConfig config, double price, TransactionCause cause) {

		switch (this) {
			case MONEY -> {
				BankerService bankerService = new BankerService();
				bankerService.withdraw(cause.of(null, player, BigDecimal.valueOf(-price), shopGroup, config.getName()));
			}

			case TOKENS -> {
				EventUserService eventUserService = new EventUserService();
				eventUserService.get(player).charge((int) Math.ceil(price));
			}
		}

	}


}
