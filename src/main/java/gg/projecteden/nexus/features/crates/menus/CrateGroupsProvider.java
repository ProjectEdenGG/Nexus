package gg.projecteden.nexus.features.crates.menus;

import com.google.api.client.util.Strings;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateGroup;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateConfigService;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Rows(6)
@Title("Crate Groups Editing")
@AllArgsConstructor
public class CrateGroupsProvider extends InventoryProvider {

	private final CrateType type;
	private final CrateGroup group;

	@Override
	public void init() {
		addCloseItem();

		// Filter Item
		contents.set(0, 8, ClickableItem.of(
			new ItemBuilder(Material.BOOK).name("&eFilter").lore("&3" + (type == null ? "All" : StringUtils.camelCase(type.name()))), e ->
				new CrateGroupsProvider(EnumUtils.nextWithLoop(CrateType.class, type.ordinal()), group).open(viewer)));

		// New Button
		contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK).name("&aCreate New").build(),
			e -> {
				CrateGroup group = new CrateGroup(type);
				CrateConfigService.get().getGroups().add(group);
				CrateConfigService.get().save();
				new CrateEditGroupProvider(this, group, false).open(viewer);
			}));

		// Add All
		List<ClickableItem> items = new ArrayList<>();
		Crates.getGroupsByType(type).forEach(group -> {
			ItemStack item = new ItemBuilder(group.getDisplayItem() == null ? new ItemStack(Material.CHEST) : group.getDisplayItem())
				.name(Strings.isNullOrEmpty(group.getTitle()) ? "Group" : group.getTitle())
				.lore("&7Click to edit", "&cShift-Click to remove").build();
			items.add(ClickableItem.of(item, e -> {
				if (e.isShiftClick()) {
					CrateConfigService.get().getGroups().remove(group);
					CrateConfigService.get().save();
					new CrateGroupsProvider(type, null).open(viewer);
				}
				else
					new CrateEditGroupProvider(this, group, false).open(viewer);
			}));
		});

		paginator().items(items).build();
	}

	@AllArgsConstructor
	private static class CrateEditGroupProvider extends InventoryProvider {

		private final InventoryProvider previous;
		private final CrateGroup group;
		private final boolean adding;

		@Override
		public void init() {
			addBackItem(e -> previous.open(viewer));

			contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.LEVER).name(adding ? "&aAdding" : "&cRemoving").build(), e-> {
				new CrateEditGroupProvider(previous, group, !adding).open(viewer);
			}));

			List<ClickableItem> items = new ArrayList<>();
			if (adding) {
				Crates.getLootByType(group.getType()).stream().filter(loot -> !group.getLootIds().contains(loot.getId())).forEach(loot -> {
					ItemStack item = getItem(loot).lore("&7Click to add").build();
					items.add(ClickableItem.of(item, e -> {
						group.getLootIds().add(loot.getId());
						CrateConfigService.get().save();
						new CrateEditGroupProvider(previous, group, adding).open(viewer);
					}));
				});
			}
			else {
				Crates.getLootByType(group.getType()).stream().filter(loot -> group.getLootIds().contains(loot.getId())).forEach(loot -> {
					ItemStack item = getItem(loot).lore("&7Click to remove").build();
					items.add(ClickableItem.of(item, e -> {
						group.getLootIds().remove(loot.getId());
						CrateConfigService.get().save();
						new CrateEditGroupProvider(previous, group, adding).open(viewer);
					}));
				});
			}

			paginator().items(items).build();
		}

		private ItemBuilder getItem(CrateLoot loot) {
			return new ItemBuilder(loot.getDisplayItem() != null ? loot.getDisplayItem().getType() :
				(loot.isActive() ? Material.ENDER_CHEST : Material.CHEST))
				.name(loot.getDisplayName());
		}

	}




}
