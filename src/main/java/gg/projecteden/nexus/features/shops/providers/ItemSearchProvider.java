package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterSearchType;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.features.titan.models.CustomCreativeItem;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Title("&0Search for item")
public class ItemSearchProvider extends ShopProvider {
	private final String filter;
	private final Consumer<ItemClickData> onChoose;

	public ItemSearchProvider(ShopProvider previousMenu, String filter, Consumer<ItemClickData> onChoose) {
		this.previousMenu = previousMenu;
		this.filter = filter;
		this.onChoose = onChoose;
	}

	@Override
	public void init() {
		super.init();

		List<ClickableItem> items = new ArrayList<>();

		for (Material material : Material.values()) {
			if (material.isLegacy())
				continue;
			if (!material.isItem())
				continue;
			if (MaterialTag.UNOBTAINABLE.isTagged(material))
				continue;
			if (MaterialTag.REQUIRES_META.isTagged(material))
				continue;

			if (!FilterSearchType.SEARCH.matches(new ItemStack(material), filter))
				continue;

			ItemStack item = new ItemBuilder(material).build();
			items.add(ClickableItem.of(item, onChoose));
		}

		for (CustomCreativeItem item : DecorationUtils.getCreativeItems())
			if (FilterSearchType.SEARCH.matches(item.getItemStack(), filter))
				items.add(ClickableItem.of(item.getItemStack(), onChoose));

		paginate(items);
	}

}
