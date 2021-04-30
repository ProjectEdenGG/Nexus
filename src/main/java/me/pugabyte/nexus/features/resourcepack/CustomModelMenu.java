package me.pugabyte.nexus.features.resourcepack;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

// TODO Meta file

@RequiredArgsConstructor
public class CustomModelMenu extends MenuUtils implements InventoryProvider {
	@NonNull
	private final CustomModelFolder folder;
	private final CustomModelMenu previousMenu;

	public CustomModelMenu() {
		this(ResourcePack.getRootFolder(), null);
	}

	public CustomModelMenu(@NonNull CustomModelFolder folder) {
		this(folder, null);
	}

	@Override
	public void open(Player viewer, int page) {
		String title = "Custom Models";
		if (!folder.getPath().equals("/"))
			title = folder.getDisplayPath();

		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0" + title))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		if (previousMenu == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> previousMenu.open(player));

		List<ClickableItem> items = new ArrayList<>();

		for (CustomModelFolder folder : folder.getFolders()) {
			CustomModel firstModel = folder.getFirstModel();
			ItemStack item = new ItemStack(Material.BARRIER);
			if (firstModel != null)
				item = firstModel.getDisplayItem();

			ItemBuilder builder = new ItemBuilder(item).name(folder.getDisplayPath()).glow();
			items.add(ClickableItem.from(builder.build(), e -> new CustomModelMenu(folder, this).open(player)));
		}

		if (!items.isEmpty()) {
			while (items.size() % 9 != 0)
				items.add(ClickableItem.empty(new ItemStack(Material.AIR)));

			for (int i = 0; i < 9; i++)
				items.add(ClickableItem.empty(new ItemStack(Material.AIR)));
		}

		for (CustomModel model : folder.getModels()) {
			ItemBuilder item = new ItemBuilder(model.getDisplayItem())
					.lore("")
					.lore("&7Click to obtain item")
					.lore("&7Shift+Click to obtain item with name");

			items.add(ClickableItem.from(item.build(), e ->
					PlayerUtils.giveItem(player, isShiftClick(e) ? model.getDisplayItem() : model.getItem())));
		}

		addPagination(player, contents, items);
	}


}
