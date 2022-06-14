package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelGroup;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelGroup.Override3;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RequiredArgsConstructor
public class CustomModelMenu extends InventoryProvider {
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
	public String getTitle() {
		String title = "Custom Models";
		if (!"/".equals(folder.getPath()))
			title = folder.getDisplayPath();

		return "&0" + title;
	}

	@Override
	public void init() {
		if (previousMenu == null)
			addCloseItem();
		else
			addBackItem(e -> previousMenu.open(player));

		List<ClickableItem> items = new ArrayList<>();

		for (CustomModelFolder folder : folder.getFolders()) {
			CustomModel firstModel = folder.getIcon();
			ItemStack item = new ItemStack(Material.BARRIER);
			if (firstModel != null)
				item = firstModel.getDisplayItem();

			ItemBuilder builder = new ItemBuilder(item).name(folder.getDisplayPath()).glow();
			items.add(ClickableItem.of(builder.build(), e -> new CustomModelMenu(folder, this).open(player)));
		}

		if (!items.isEmpty()) {
			while (items.size() % 9 != 0)
				items.add(ClickableItem.NONE);

			for (int i = 0; i < 9; i++)
				items.add(ClickableItem.NONE);
		}

		for (CustomModel model : folder.getModels()) {
			if ("icon".equals(model.getFileName()))
				continue;

			ItemBuilder item = new ItemBuilder(model.getDisplayItem())
					.lore("")
					.lore("&7Click to obtain item")
					.lore("&7Shift+Click to obtain item with name");

			items.add(ClickableItem.of(item.build(), e -> PlayerUtils.giveItem(player, e.isShiftClick() ? model.getDisplayItem() : model.getItem())));
		}

		paginator().items(items).build();
	}

	public static void load() {
		for (String path : getFolderPaths())
			addFoldersRecursively(path);
	}

	private static Set<String> getFolderPaths() {
		Set<String> paths = new HashSet<>();

		for (CustomModelGroup group : ResourcePack.getModelGroups())
			for (Override3 override : group.getOverrides())
				paths.add(override.getFolderPath());

		return new TreeSet<>(paths);
	}

	private static void addFoldersRecursively(String path) {
		String[] folders = path.split("/");

		String walk = "";
		for (String folder : folders) {
			if (folder.isEmpty() || "/".equals(folder))
				continue;

			String parent = walk;
			walk += "/" + folder;
			addFolder(walk, folder, parent);
		}
	}

	private static void addFolder(String walk, String folder, String parent) {
		CustomModelFolder existing = ResourcePack.getRootFolder().getFolder(walk);
		if (existing == null)
			if (parent.isEmpty())
				ResourcePack.getRootFolder().addFolder(folder);
			else
				ResourcePack.getRootFolder().getFolder(parent).addFolder(folder);
	}

}
