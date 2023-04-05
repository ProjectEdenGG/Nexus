package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Title("&0Search for item")
public class ItemSearchProvider extends ShopProvider {
	private final Function<Material, Boolean> filter;
	private final Consumer<ItemClickData> onChoose;

	public ItemSearchProvider(ShopProvider previousMenu, Function<Material, Boolean> filter, Consumer<ItemClickData> onChoose) {
		this.previousMenu = previousMenu;
		this.filter = filter;
		this.onChoose = onChoose;
	}

	@Override
	public void init() {
		super.init();

		List<ClickableItem> items = new ArrayList<>();

		for (Material material : Material.values()) {
			if (material.isLegacy()) continue;
			if (!material.isItem()) continue;
			if (MaterialTag.UNOBTAINABLE.isTagged(material)) continue;
			if (MaterialTag.REQUIRES_META.isTagged(material)) continue;

			if (filter != null && !filter.apply(material))
				continue;

			ItemStack item = new ItemBuilder(material)
					.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
					.build();
			items.add(ClickableItem.of(item, onChoose));
		}

		paginate(items);
	}

}
