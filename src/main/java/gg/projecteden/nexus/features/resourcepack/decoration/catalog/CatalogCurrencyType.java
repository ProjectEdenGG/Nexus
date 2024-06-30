package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.TypeConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("Convert2MethodRef")
@Getter
@AllArgsConstructor
public enum CatalogCurrencyType {
	MONEY(85.0,
			vars -> new BankerService().has(vars.getPlayer(), vars.getPrice(), vars.getShopGroup()),
			vars -> new BankerService().withdrawal(TransactionCause.DECORATION_STORE.of(null, vars.getPlayer(), BigDecimal.valueOf(-vars.getPrice()), vars.getShopGroup(), vars.getConfig().getName())),
			typeConfig -> typeConfig.money(),
			price -> StringUtils.prettyMoney(price),
			price -> "&3Price: &a"

	),

	TOKENS(50.0,
			vars -> new EventUserService().get(vars.getPlayer()).getTokens() >= vars.getPrice(),
			vars -> new EventUserService().get(vars.getPlayer()).charge((int) Math.ceil(vars.getPrice())),
			typeConfig -> (double) typeConfig.tokens(),
			price -> price + "tokens",
			price -> "&3Tokens: &a" + price
	),

	;

	final double skullPrice;
	final Predicate<Variables> checkFunds;
	final Consumer<Variables> withdrawal;
	final Function<TypeConfig, Double> price;
	final Function<Double, String> pricePretty;
	final Function<Double, String> priceLabel;

	@Getter
	@AllArgsConstructor
	private static class Variables {
		Player player;
		DecorationConfig config;
		ShopGroup shopGroup;
		double price;
	}

	public boolean hasFunds(Player player, DecorationConfig config, ShopGroup shopGroup, double price) {
		return this.checkFunds.test(new Variables(player, config, shopGroup, price));
	}

	public void withdrawal(Player player, DecorationConfig config, ShopGroup shopGroup, double price) {
		this.withdrawal.accept(new Variables(player, config, shopGroup, price));
	}

	public Double getPriceDecor(DecorationConfig config) {
		DecorationType type = DecorationType.of(config);
		if (type == null)
			return null;

		TypeConfig typeConfig = type.getTypeConfig();
		if (typeConfig == null)
			return null;

		double price = this.price.apply(typeConfig);

		if (price <= -1)
			return null;

		return price;
	}

	public ItemStack getPricedCatalogItem(Player viewer, DecorationConfig config) {
		Double price = getPriceDecor(config);
		if (price == null)
			return null;

		if (DecorationUtils.hasBypass(viewer))
			price = 0d;

		String priceStr = this.priceLabel.apply(price) + this.pricePretty.apply(price);

		if (price <= 0)
			priceStr = "free";

		return config.getItemBuilder().lore("", priceStr).build();
	}

	public String getPriceActionBar(@NonNull String name, double price) {
		String priceStr = this.pricePretty.apply(price);

		if (price <= 0)
			priceStr = "free";

		return "&3Buy &e" + name + " &3for &a" + priceStr;
	}
}
