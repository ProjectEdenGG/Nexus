package gg.projecteden.nexus.features.resourcepack.decoration.store;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.MultiState;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BuyableData {
	ItemStack displayItem;

	@Getter
	DecorationConfig decorationConfig;
	DecorationStoreType storeType;

	public BuyableData(ItemStack itemStack, DecorationStoreType storeType) {
		this.displayItem = itemStack;
		this.storeType = storeType;

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

	public void showPrice(Player player) {
		String name = getName();
		if (name == null)
			return;

		if (isDecoration() && decorationConfig instanceof Art art) {
			name = art.getArtTitle();
		}

		Integer price = getPrice();
		if (price == null)
			return;

		ActionBarUtils.sendActionBar(player, storeType.getCurrency().getPriceActionBar(name, price), TickTime.TICK.x(4), false);
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

	public Integer getPrice() {
		return getPrice(displayItem, storeType);
	}

	//

	public static boolean isBuyable(Player debugger, ItemStack itemStack, DecorationStoreType storeType) {
		Integer price = getPrice(itemStack, storeType);
		DecorationStoreManager.debug(debugger, "Price: " + price);
		return price != null;
	}

	public static @Nullable Integer getPrice(ItemStack itemStack, DecorationStoreType storeType) {
		// HDB Skull
		if (itemStack.getType().equals(Material.PLAYER_HEAD))
			return storeType.getCurrency().getPriceSkull(storeType);

		// Decoration
		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config != null)
			return getPrice(config, storeType);

		// Unknown
		return null;
	}

	public static @Nullable Integer getPrice(DecorationConfig config, DecorationStoreType storeType) {
		if (config == null)
			return null;

		if (config instanceof MultiState multiState) {
			ItemModelType itemModelType = multiState.getBaseItemModel();
			if (!itemModelType.is(config))
				return getPrice(DecorationConfig.of(itemModelType), storeType);
		}

		DecorationType type = DecorationType.of(config);
		if (type != null && type.getTypeConfig().unbuyable())
			return null;

		return config.getCatalogPrice(storeType);
	}
}
