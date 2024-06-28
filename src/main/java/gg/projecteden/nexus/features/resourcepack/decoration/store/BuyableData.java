package gg.projecteden.nexus.features.resourcepack.decoration.store;

import com.mojang.datafixers.util.Pair;
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
	ItemStack baseItem;

	@Getter
	DecorationConfig decorationConfig;
	CatalogCurrencyType currency;

	public BuyableData(ItemStack itemStack, CatalogCurrencyType currency) {
		this.baseItem = itemStack;
		this.currency = currency;

		decorationConfig = DecorationConfig.of(baseItem);
	}

	public ItemStack getItem(Player viewer) {
		ItemBuilder baseItemBuilder = new ItemBuilder(baseItem);

		if (isDecoration()) {
			ItemBuilder configItemBuilder = new ItemBuilder(decorationConfig.getCatalogItem(viewer, currency));
			Color dyeColor = baseItemBuilder.dyeColor();
			if (dyeColor != null)
				configItemBuilder.dyeColor(dyeColor);

			return configItemBuilder.build();
		}

		return baseItemBuilder.name("&f" + getName()).build();
	}


	public @Nullable Pair<String, Double> getNameAndPrice() {
		Double price = getPrice(baseItem, currency);
		if (price == null) return null;

		String name = getName();
		if (name == null) return null;

		return new Pair<>(name, price);
	}

	public boolean isHDB() {
		return baseItem.getType().equals(Material.PLAYER_HEAD);
	}

	public boolean isDecoration() {
		return decorationConfig != null;
	}

	public @Nullable String getName() {
		if (isHDB()) {
			String id = Nexus.getHeadAPI().getItemID(baseItem);
			if (id == null)
				return "Player Head";

			ItemStack item = Nexus.getHeadAPI().getItemHead(id);
			return StringUtils.stripColor(item.getItemMeta().getDisplayName());
		} else if (isDecoration())
			return decorationConfig.getName();
		else
			return null;
	}

	public void showPrice(Player player) {
		Pair<String, Double> namePrice = getNameAndPrice();
		if (namePrice == null)
			return;

		ActionBarUtils.sendActionBar(
				player,
				"&3Buy &e" + namePrice.getFirst() + " &3for &a" + StringUtils.prettyMoney(namePrice.getSecond()),
				TickTime.TICK.x(4),
				false
		);
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
			return 85.0;

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
