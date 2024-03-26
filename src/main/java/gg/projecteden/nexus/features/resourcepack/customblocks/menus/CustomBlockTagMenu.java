package gg.projecteden.nexus.features.resourcepack.customblocks.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomBlockTagMenu extends InventoryProvider {
	private final CustomBlockTag customBlockTag;

	public CustomBlockTagMenu(CustomBlockTag tag) {
		this.customBlockTag = tag;
	}

	@Override
	public String getTitle() {
		return "&3" + StringUtils.camelCase(customBlockTag.getKey().getKey());
	}

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();
		customBlockTag.getValues().forEach(customBlock -> {
			ItemStack customBlockItem = customBlock.get().getItemStack();
			if (!Nullables.isNullOrAir(customBlockItem)) {
				ItemStack item = new ItemBuilder(customBlock.get().getItemStack()).lore(customBlock.name().toLowerCase()).build();
				items.add(ClickableItem.empty(item));
			}
		});

		paginate(items);
	}

}
