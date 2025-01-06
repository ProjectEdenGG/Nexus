package gg.projecteden.nexus.features.crates.menus;

import com.google.common.base.Strings;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateConfigService;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Title("Crate Editing")
public class CrateEditMenu {

	@NoArgsConstructor
	@AllArgsConstructor
	public static class CrateEditProvider extends InventoryProvider {
		private CrateType filter;
		private CrateLoot editing;

		@Override
		protected int getRows(Integer page) {
			return editing == null ? 6 : 3;
		}

		@Override
		public void init() {
			if (editing != null) {
				// Back Item
				addBackItem(e -> {
					save(viewer.getOpenInventory().getTopInventory());
					new CrateEditProvider(filter, null).open(viewer);
				});

				// Save Item
				contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.NETHER_STAR).name("&eSave").build(), e -> {
					save(viewer.getOpenInventory().getTopInventory());
					new CrateEditProvider(filter, editing).open(viewer);
				}));

				// SettingsItem
				contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.WRITABLE_BOOK)
					.name("&eSettings").lore("&3Click to edit").build(), e -> {
					save(viewer.getOpenInventory().getTopInventory());
					new LootSettingsProvider(filter, editing).open(viewer);
				}));

				// Toggle Active Item
				contents.set(0, 6, ClickableItem.of(new ItemBuilder(editing.isActive() ? Material.ENDER_CHEST : Material.CHEST)
					.name("&eToggle Active").lore("&3" + editing.isActive()).build(), e -> {
					save(viewer.getOpenInventory().getTopInventory());
					editing.setActive(!editing.isActive());
					CrateConfigService.get().save();
					new CrateEditProvider(filter, editing).open(viewer);
				}));

				int i = 1;
				int j = 0;
				for (ItemStack itemStack : editing.getItems()) {
					contents.set(i, j, ClickableItem.empty(itemStack));
					if (j == 8) {
						j = 0;
						i++;
					} else j++;
				}

				for (int row = 1; row <= 2; row++)
					for (int column = 0; column <= 8; column++)
						contents.setEditable(row, column, true);
			} else {
				// Close Item
				addCloseItem();

				// Filter Item
				contents.set(0, 8, ClickableItem.of(
					new ItemBuilder(Material.BOOK).name("&eFilter").lore("&3" + (filter == null ? "All" : StringUtils.camelCase(filter.name()))), e ->
						new CrateEditProvider(EnumUtils.nextWithLoop(CrateType.class, filter.ordinal()), null).open(viewer)));

				// New Button
				contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK).name("&aCreate New").build(),
					e -> {
						CrateLoot loot = new CrateLoot(filter);
						CrateConfigService.get().getLoot().add(loot);
						CrateConfigService.get().save();
						new CrateEditProvider(filter, loot).open(viewer);
					}));

				// Groups button
				contents.set(0, 6, ClickableItem.of(new ItemBuilder(Material.SHULKER_BOX).name("&eEdit Groups").build(), e-> {
					new CrateGroupsProvider(filter, null).open(viewer);
				}));

				// Loot Items
				Pagination page = contents.pagination();
				List<ClickableItem> items = new ArrayList<>();
				Crates.getLootByType(filter).forEach(loot -> {
					ItemBuilder builder = new ItemBuilder(loot.getDisplayItem() != null ? loot.getDisplayItem().getType() :
						(loot.isActive() ? Material.ENDER_CHEST : Material.CHEST))
						.name(loot.getDisplayName())
						.lore("&3Type: &e" + StringUtils.camelCase(loot.getType()))
						.lore(" ")
						.lore("&eLeft-Click &3to edit")
						.lore("&eRight-Click &3to enable/disable")
						.lore("&cShift-Click to Delete");

					if (loot.getDisplayItem() != null)
						builder.modelId(new ItemBuilder(loot.getDisplayItem()).modelId());

					ItemStack item = builder.build();
					items.add(ClickableItem.of(item, e -> {
						InventoryClickEvent event = (InventoryClickEvent) e.getEvent();
						if (event.isShiftClick()) {
							ConfirmationMenu.builder()
								.title("Delete " + loot.getDisplayName() + "?")
								.onConfirm(e2 -> {
									CrateConfigService.get().getLoot().remove(loot);
									CrateConfigService.get().save();
									new CrateEditProvider(filter, null).open(viewer);
								}).open(viewer);
							return;
						}
						if (event.isLeftClick()) {
							new CrateEditProvider(filter, loot).open(viewer);
							return;
						}
						if (event.isRightClick()) {
							loot.setActive(!loot.isActive());
							CrateConfigService.get().save();
							new CrateEditProvider(filter, null).open(viewer, page.getPage());
						}
					}));
				});

				paginate(items);
			}
		}

		public void save(List<ItemStack> contents) {
			List<ItemStack> items = new ArrayList<>();
			for (int i = 9; i < 27; i++)
				if (!Nullables.isNullOrAir(contents.get(i)))
					items.add(contents.get(i));
			editing.setItems(items);
			CrateConfigService.get().save();
		}

		public void save(Inventory inventory) {
			save(Arrays.asList(inventory.getContents()));
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			if (editing != null)
				save(contents);
		}

	}

	@Rows(2)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LootSettingsProvider extends InventoryProvider {

		private CrateType filter;
		private CrateLoot editing;

		@Override
		public void init() {
			addBackItem(e -> new CrateEditProvider(filter, editing).open(viewer));

			// Display Item
			ItemStack displayItem = editing.getDisplayItemWithNull();
			if (displayItem == null)
				displayItem = new ItemBuilder(Material.ITEM_FRAME).name("&eDisplay Item")
					.lore("&3This will change into any item")
					.lore("&3click with it. It will be the")
					.lore("&3item that spawns on crate opening")
					.build();
			contents.set(0, 2, ClickableItem.of(displayItem, e -> {
				InventoryClickEvent event = (InventoryClickEvent) e.getEvent();
				ItemStack display = Nullables.isNullOrAir(event.getCursor()) ? null : event.getCursor();
				editing.setDisplayItem(display);
				CrateConfigService.get().save();
				Tasks.wait(1, () -> {
					ItemStack item = event.getCursor();
					viewer.setItemOnCursor(null);
					new LootSettingsProvider(filter, editing).open(viewer);
					viewer.setItemOnCursor(item);
				});
			}));

			// Title Item
			contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.WRITABLE_BOOK).name("&eDisplay Name")
					.lore("&3" + editing.getDisplayName()).build(),
				e -> {
					new AnvilGUI.Builder()
						.text(editing.getDisplayName())
						.onComplete(((player1, text) -> {
							editing.setTitle(text);
							CrateConfigService.get().save();
							Tasks.wait(1, () -> new CrateEditProvider(filter, editing).open(viewer));
							return AnvilGUI.Response.text(text);
						}))
						.onClose(player1 -> Tasks.wait(1, () -> new LootSettingsProvider(filter, editing).open(viewer)))
						.plugin(Nexus.getInstance())
						.open(viewer);
				}));

			// CrateType Item
			contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.PAPER).name("&eCrate Type")
					.lore("&3" + StringUtils.camelCase(editing.getType().name())).build(),
				e -> {
					editing.setType(EnumUtils.nextWithLoop(CrateType.class, filter.ordinal()));
					CrateConfigService.get().save();
					new LootSettingsProvider(filter, editing).open(viewer);
				}));

			// Weight Item
			contents.set(0, 5, ClickableItem.of(new ItemBuilder(Material.ANVIL).name("&eWeight")
					.lore("&3Current Value: &e" + editing.getWeight()).build(),
				e -> {
					new AnvilGUI.Builder()
						.text("" + editing.getWeight())
						.onComplete((player1, text) -> {
							try {
								double d = Double.parseDouble(text);
								editing.setWeight(d);
								CrateConfigService.get().save();
							} catch (NumberFormatException ex) {
								PlayerUtils.send(player1, Crates.PREFIX + "Weight must be a number value");
							}
							Tasks.wait(1, () -> new LootSettingsProvider(filter, editing).open(viewer));
							return AnvilGUI.Response.close();
						})
						.onClose($ -> Tasks.wait(1, () -> new LootSettingsProvider(filter, editing).open(viewer)))
						.plugin(Nexus.getInstance())
						.open(viewer);
				}));

			// Announce item
			contents.set(0, 6, ClickableItem.of(new ItemBuilder(Material.GOAT_HORN).name("&eAnnounce")
					.lore("&3Current Value: " + editing.isShouldAnnounce()).build(),
				e -> {
					editing.setShouldAnnounce(!editing.isShouldAnnounce());
					CrateConfigService.get().save();
					new LootSettingsProvider(filter, editing).open(viewer);
				}));

			// Announce Message Item
			contents.set(0, 7, ClickableItem.of(new ItemBuilder(Material.NOTE_BLOCK).name("Announce Message")
					.lore(new ArrayList<>() {{
						add("&7Use &e%player% &7for a player name");
						add("&7Use &e%title% &7for a loot title");
						add("&3Current Value:");
						addAll(StringUtils.loreize(Strings.isNullOrEmpty(editing.getAnnouncement()) ? "" : editing.getAnnouncement()));
					}}).build(),
				e -> {
					viewer.closeInventory();
					new JsonBuilder(Crates.PREFIX + "Current announcement for loot id &e" + editing.getId() + ":").send(viewer);
					new JsonBuilder("&3" + (Strings.isNullOrEmpty(editing.getAnnouncement()) ? "&cnull" : editing.getAnnouncement()))
						.group()
						.next(" &7&l[Edit]").suggest("/crates edit announcement set " + editing.getId() + " ").group()
						.next(" &c&l[Reset]").suggest("/crates edit announcement reset " + editing.getId()).group()
						.send(viewer);
				}));


			// Commands on Complete
			contents.set(1, 0, ClickableItem.of(new ItemBuilder(Material.BOOK).name("&eCommands on Open")
					.lore("&3Commands which should be ran",
						"&3when this loot is opened",
						" ",
						"&7Click to add another",
						"&7Use &e%player% &7for a player name").build(),
				e -> {
					new AnvilGUI.Builder()
						.text("")
						.onComplete((player1, text) -> {
							String command = text;
							if (command.startsWith("/"))
								command = command.substring(1);
							editing.getCommandsNoSlash().add(command);
							CrateConfigService.get().save();
							Tasks.wait(1, () -> new LootSettingsProvider(filter, editing).open(viewer));
							return AnvilGUI.Response.close();
						})
						.onClose($ -> Tasks.wait(1, () -> new LootSettingsProvider(filter, editing).open(viewer)))
						.plugin(Nexus.getInstance())
						.open(viewer);
				}));

			// Commands
			for (int i = 0; i < editing.getCommandsNoSlash().size(); i++) {
				int finalI = i;
				contents.set(1, i + 1, ClickableItem.of(new ItemBuilder(Material.PAPER).name(editing.getCommandsNoSlash().get(i))
						.lore("&cShift-Click to remove").build(),
					e -> {
						editing.getCommandsNoSlash().remove(finalI);
						CrateConfigService.get().save();
						new LootSettingsProvider(filter, editing).open(viewer);
					}));
			}

		}


	}

}
