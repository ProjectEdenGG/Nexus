package gg.projecteden.nexus.features.resourcepack.decoration.store;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.TypeConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("Convert2MethodRef")
@Getter
@AllArgsConstructor
public enum DecorationStoreCurrencyType {
	MONEY(85,
		vars -> new BankerService().has(vars.getPlayer(), vars.getPrice(), vars.getShopGroup()),
		vars -> new BankerService().withdraw(TransactionCause.DECORATION_STORE.of(null, vars.getPlayer(), BigDecimal.valueOf(-vars.getPrice()), vars.getShopGroup(), vars.getProductName())),
		typeConfig -> typeConfig.money(),
		price -> StringUtils.prettyMoney(price),
		price -> "&3Price: &a"
	),

	TOKENS(15,
		vars -> new EventUserService().get(vars.getPlayer()).getTokens() >= vars.getPrice(),
		vars -> new EventUserService().get(vars.getPlayer()).charge((int) Math.ceil(vars.getPrice())),
		typeConfig -> typeConfig.tokens(),
		price -> price + " tokens",
		price -> "&3Tokens: &a" + price
	),

	;

	private final int skullPrice;
	private final Predicate<Variables> checkFunds;
	private final Consumer<Variables> withdraw;
	private final Function<TypeConfig, Integer> price;
	private final Function<Integer, String> pricePretty;
	private final Function<Integer, String> priceLabel;

	@Getter
	@AllArgsConstructor
	private static class Variables {
		Player player;
		ItemStack itemStack;
		ShopGroup shopGroup;
		double price;

		public String getProductName() {
			if (this.itemStack.getType() == Material.PLAYER_HEAD) {
				String id = Nexus.getHeadAPI().getItemID(this.itemStack);
				if (id == null)
					return "Player Head";

				return id;
			}

			DecorationConfig config = DecorationConfig.of(itemStack);
			if (config != null) {
				return config.getName();
			}

			throw new InvalidInputException("Unknown decoration type of: " + itemStack.getType() + ", model = " + Model.of(itemStack));
		}
	}

	public boolean hasFunds(Player player, ItemStack itemStack, ShopGroup shopGroup, double price) {
		return this.checkFunds.test(new Variables(player, itemStack, shopGroup, price));
	}

	public void withdraw(Player player, ItemStack itemStack, ShopGroup shopGroup, double price) {
		this.withdraw.accept(new Variables(player, itemStack, shopGroup, price));
	}

	public Integer getPriceSkull(DecorationStoreType storeType) {
		return storeType.getDiscountedPrice(this.skullPrice);
	}

	public Integer getPriceDecor(DecorationConfig config, DecorationStoreType storeType) {
		DecorationType type = DecorationType.of(config);
		if (type == null)
			return null;

		TypeConfig typeConfig = type.getTypeConfig();
		if (typeConfig == null)
			return null;

		int price = this.price.apply(typeConfig);

		if (price <= -1)
			return null;

		return storeType.getDiscountedPrice(price);
	}

	public ItemStack getPricedCatalogItem(Player viewer, DecorationConfig config, DecorationStoreType storeType) {
		Integer price = getPriceDecor(config, storeType);
		if (price == null)
			return null;

		if (DecorationUtils.hasBypass(viewer))
			price = 0;

		String priceStr = this.priceLabel.apply(price) + this.pricePretty.apply(price);

		if (price <= 0)
			priceStr = "&aFree";

		return config.getItemBuilder().lore("", priceStr).build();
	}

	public String getPriceActionBar(@NonNull String name, int price) {
		String priceStr = this.pricePretty.apply(price);

		if (price <= 0)
			priceStr = "Free";

		return "&3Buy &e" + name + " &3for &a" + priceStr;
	}
}
