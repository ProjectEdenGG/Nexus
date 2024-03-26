package gg.projecteden.nexus.features.resourcepack.customblocks.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.List;

public class CustomBlockSearchMenu extends InventoryProvider {
	@NonNull String filter;
	@NonNull List<CustomBlock> customBlocks;

	public CustomBlockSearchMenu(@NonNull String filter, @NonNull List<CustomBlock> customBlocks) {
		this.filter = filter;
		this.customBlocks = customBlocks;
	}

	@Override
	public String getTitle() {
		return "Containing: \"" + filter + "\"";
	}

	@Override
	public void init() {
		addCloseItem();

		LinkedHashSet<ClickableItem> items = new LinkedHashSet<>();
		for (CustomBlock customBlock : customBlocks) {
			ItemStack item = new ItemBuilder(customBlock.get().getItemStack()).build();
			items.add(ClickableItem.of(item, e -> PlayerUtils.giveItem(viewer, item)));
		}

		paginate(items);
	}
}
