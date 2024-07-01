package gg.projecteden.nexus.features.resourcepack.decoration.store;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.CatalogCurrencyType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.MultiState;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BuyableData {
	ItemStack displayItem;

	@Getter
	DecorationConfig decorationConfig;
	CatalogCurrencyType currency;

	public BuyableData(ItemStack itemStack, CatalogCurrencyType currency) {
		this.displayItem = itemStack;
		this.currency = currency;

		decorationConfig = DecorationConfig.of(displayItem);
	}

	public boolean isHDB() {
		return displayItem.getType().equals(Material.PLAYER_HEAD);
	}

	public boolean isDecoration() {
		return decorationConfig != null;
	}

	public ItemStack getItem() {
		ItemBuilder displayItemBuilder = new ItemBuilder(displayItem);

		if (isDecoration()) {
			ItemBuilder configItemBuilder = new ItemBuilder(decorationConfig.getItem());
			Color dyeColor = displayItemBuilder.dyeColor();
			if (dyeColor != null) {
				configItemBuilder.updateDecorationLore(true);
				configItemBuilder.dyeColor(dyeColor);
			}

			return configItemBuilder.build();
		}

		return displayItemBuilder.name("&f" + getName()).build();
	}

	public void showPrice(Player player, CatalogCurrencyType currency) {
		String name = getName();
		if (name == null)
			return;

		Double price = getPrice(displayItem, currency);
		if (price == null)
			return;

		ActionBarUtils.sendActionBar(player, currency.getPriceActionBar(name, price), TickTime.TICK.x(4), false);
	}

	public @Nullable String getName() {
		if (isHDB()) {
			String id = Nexus.getHeadAPI().getItemID(displayItem);
			if (id == null)
				return "Player Head";

			ItemStack item = Nexus.getHeadAPI().getItemHead(id);
			return StringUtils.stripColor(item.getItemMeta().getDisplayName());
		} else if (isDecoration())
			return decorationConfig.getName();
		else
			return null;
	}

	public Double getPrice(CatalogCurrencyType currency) {
		return getPrice(displayItem, currency);
	}

	//

	public static boolean isBuyable(Player debugger, ItemStack itemStack, @NonNull CatalogCurrencyType currency) {
		Double price = getPrice(itemStack, currency);
		DecorationStoreManager.debug(debugger, "Price: " + price);
		return price != null;
	}

	public static @Nullable Double getPrice(ItemStack itemStack, @NonNull CatalogCurrencyType currency) {
		// HDB Skull
		if (itemStack.getType().equals(Material.PLAYER_HEAD))
			return currency.getSkullPrice();

		// Decoration
		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config != null)
			return getPrice(config, currency);

		// Unknown
		return null;
	}

	public static @Nullable Double getPrice(DecorationConfig config, @NonNull CatalogCurrencyType currency) {
		if (config == null)
			return null;

		if (config instanceof MultiState multiState) {
			CustomMaterial baseMaterial = multiState.getBaseMaterial();
			if (!baseMaterial.is(config))
				return getPrice(DecorationConfig.of(baseMaterial), currency);
		}

		DecorationType type = DecorationType.of(config);
		if (type != null && type.getTypeConfig().unbuyable())
			return null;

		return config.getCatalogPrice(currency);
	}
}
