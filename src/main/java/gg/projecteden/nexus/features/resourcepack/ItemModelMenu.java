package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance;
import gg.projecteden.nexus.features.resourcepack.models.files.ItemModelFolder;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ItemModelMenu extends InventoryProvider {
	@NonNull
	private final ItemModelFolder folder;
	private final ItemModelMenu previousMenu;

	public ItemModelMenu() {
		this(ResourcePack.getRootFolder(), null);
	}

	public ItemModelMenu(@NonNull ItemModelFolder folder) {
		this(folder, null);
	}

	@Override
	public String getTitle() {
		String title = "Custom Models";
		if (!"/".equals(folder.getPath())) {
			title = folder.getDisplayPath();

			String[] folders = title.split("/");
			if (folders.length > 2) {
				title = "../" + folders[folders.length - 2] + "/" + folders[folders.length - 1];
			}
		}

		return "&0" + title;
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		List<ClickableItem> items = new ArrayList<>();

		for (ItemModelFolder folder : folder.getFolders()) {
			ItemModelInstance firstModel = folder.getIcon();
			ItemStack item = new ItemStack(Material.BARRIER);
			if (firstModel != null)
				item = firstModel.getDisplayItem();

			ItemBuilder builder = new ItemBuilder(item).name(folder.getDisplayPath()).glow();
			items.add(ClickableItem.of(builder.build(), e -> new ItemModelMenu(folder, this).open(viewer)));
		}

		if (!items.isEmpty()) {
			while (items.size() % 9 != 0)
				items.add(ClickableItem.NONE);

			for (int i = 0; i < 9; i++)
				items.add(ClickableItem.NONE);
		}

		for (ItemModelInstance model : folder.getModels()) {
			if ("icon".equals(model.getFileName()))
				continue;

			ItemBuilder item = new ItemBuilder(model.getDisplayItem())
					.lore("&e" + StringUtils.camelCase(model.getMaterial()) + ": " + model.getItemModel())
					.lore("&eOld Data: %s (%d)".formatted(model.getOldMaterial(), model.getOldCustomModelData()))
					.lore("")
					.lore("&7Click to obtain item")
					.lore("&7Shift+Click to obtain item with name");

			items.add(ClickableItem.of(item.build(), e -> PlayerUtils.giveItem(viewer, e.isShiftClick() ? model.getDisplayItem() : model.getItem())));
		}

		paginate(items);
	}

}
