package gg.projecteden.nexus.features.resourcepack.customblocks.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTab;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

public class CustomBlockCreativeMenu extends InventoryProvider {
	@NonNull CustomBlockTab currentTab;

	public CustomBlockCreativeMenu(@NotNull CustomBlockTab tab) {
		this.currentTab = tab;
	}

	@Override
	public String getTitle() {
		return currentTab.getMenuTitle();
	}

	@Override
	public void init() {
		LinkedHashSet<ClickableItem> items = new LinkedHashSet<>();

		if (currentTab == CustomBlockTab.ALL) {
			addCloseItem();

			for (CustomBlockTab tab : CustomBlockTab.getMenuTabs()) {
				ItemStack item = new ItemBuilder(CustomBlock.getBy(tab).get(0).get().getItemStack()).name(StringUtils.camelCase(tab)).build();

				items.add(ClickableItem.of(item, e -> new CustomBlockCreativeMenu(tab).open(viewer)));
			}

		} else {
			addBackItem(e -> new CustomBlockCreativeMenu(CustomBlockTab.ALL).open(viewer));

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
