package gg.projecteden.nexus.features.resourcepack.customblocks.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTab;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

public class CustomBlockCatalogMenu extends InventoryProvider {
	@NonNull CustomBlockTab currentTab;

	public CustomBlockCatalogMenu(@NotNull CustomBlockTab tab) {
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
				if (CustomBlockTab.ALL == tab)
					continue;

				// TODO: Disable tripwire customblocks
				if (CustomBlockTab.FLORA == tab || CustomBlockTab.ROCKS == tab) {
					if (ICustomTripwire.isNotEnabled())
						continue;
				}
				//

				ItemStack item = new ItemBuilder(CustomBlock.getBy(tab).getFirst().get().getItemStack()).name(tab.getMenuTitle()).build();

				items.add(ClickableItem.of(item, e -> new CustomBlockCatalogMenu(tab).open(viewer)));
			}

		} else {
			addBackItem(e -> new CustomBlockCatalogMenu(CustomBlockTab.ALL).open(viewer));

			for (CustomBlock customBlock : CustomBlock.getBy(currentTab)) {
				// TODO: Disable tripwire customblocks
				if (ICustomTripwire.isNotEnabled() && customBlock.get() instanceof ICustomTripwire) {
					continue;
				}
				//

				ItemStack item = customBlock.get().getItemStack();
				ItemBuilder displayItem = new ItemBuilder(item);

				items.add(ClickableItem.of(displayItem, e -> PlayerUtils.giveItem(viewer, item)));
			}
		}

		paginate(items);
	}
}
