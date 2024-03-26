package gg.projecteden.nexus.features.resourcepack.customblocks.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTab;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;

public class CustomBlockCreativeMenu extends InventoryProvider {
	@Nullable CustomBlockTab currentTab;

	public CustomBlockCreativeMenu(@Nullable CustomBlockTab tab) {
		this.currentTab = tab;
	}

	@Override
	public String getTitle() {
		if (currentTab == null)
			return "Custom Blocks";

		return currentTab.getMenuTitle();
	}

	@Override
	public void init() {
		LinkedHashSet<ClickableItem> items = new LinkedHashSet<>();

		if (currentTab == null) {
			addCloseItem();

			for (CustomBlockTab tab : CustomBlockTab.getMenuTabs()) {
				ItemStack item = new ItemBuilder(CustomBlock.getBy(tab).get(0).get().getItemStack()).name(StringUtils.camelCase(tab)).build();

				items.add(ClickableItem.of(item, e -> new CustomBlockCreativeMenu(tab).open(viewer)));
			}

		} else {
			addBackItem(e -> new CustomBlockCreativeMenu(null).open(viewer));

			LinkedHashSet<ItemStack> uniqueItems = new LinkedHashSet<>();
			for (CustomBlock customBlock : CustomBlock.getBy(currentTab)) {
				ItemStack item = customBlock.get().getItemStack();
				uniqueItems.add(item);
			}

			for (ItemStack customBlockItem : uniqueItems) {
				items.add(ClickableItem.of(customBlockItem, e -> PlayerUtils.giveItem(viewer, customBlockItem)));
			}
		}

		paginate(items);
	}
}
