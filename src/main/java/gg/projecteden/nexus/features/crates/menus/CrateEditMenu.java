package gg.projecteden.nexus.features.crates.menus;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.crates.models.CrateLoot;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Title("Crate Editing")
public class CrateEditMenu {

	@NoArgsConstructor
	@AllArgsConstructor
	public static class CrateEditProvider extends InventoryProvider implements Listener {
		private CrateType filter;
		private CrateLoot editing;

		@Override
		protected int getRows() {
			return editing == null ? 6 : 3;
		}

		@Override
		public void init() {
			if (editing != null) {
				// Back Item
				addBackItem(e -> {
					save(player.getOpenInventory().getTopInventory(), editing);
					new CrateEditProvider(filter, null).open(player);
				});

				// Save Item
				contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.NETHER_STAR).name("&eSave").build(), e -> {
					save(player.getOpenInventory().getTopInventory(), editing);
					new CrateEditProvider(filter, editing).open(player);
				}));

				// Weight Item
				contents.set(0, 2, ClickableItem.of(new ItemBuilder(Material.ANVIL).name("&eWeight")
					.lore("&3Current Value: &e" + editing.getWeight()).build(), e -> {
					save(player.getOpenInventory().getTopInventory(), editing);
					new AnvilGUI.Builder()
						.text("" + editing.getWeight())
						.onComplete((player1, text) -> {
							try {
								double d = Double.parseDouble(text);
								editing.setWeight(d);
								editing.update();
							} catch (NumberFormatException ex) {
								PlayerUtils.send(player1, Crates.PREFIX + "Weight must be a number value");
							}
							new CrateEditProvider(filter, editing).open(player);
							return AnvilGUI.Response.close();
						})
						.onClose($ -> new CrateEditProvider(filter, editing).open(player))
						.plugin(Nexus.getInstance())
						.open(player);
				}));

				// CrateType Item
				contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.PAPER).name("&eCrate Type")
					.lore("&3" + StringUtils.camelCase(editing.getType().name())).build(), e -> {
					save(player.getOpenInventory().getTopInventory(), editing);
					editing.setType(EnumUtils.nextWithLoop(CrateType.class, filter.ordinal()));
					editing.update();
					new CrateEditProvider(filter, editing).open(player);
				}));

				// Title Item
				contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.WRITABLE_BOOK).name("&eDisplay Name")
					.lore("&3" + editing.getTitle()).build(), e -> {
					save(player.getOpenInventory().getTopInventory(), editing);
					new AnvilGUI.Builder()
						.text(editing.getTitle())
						.onComplete(((player1, text) -> {
							editing.setTitle(text);
							editing.update();
							new CrateEditProvider(filter, editing).open(player);
							return AnvilGUI.Response.text(text);
						}))
						.onClose(player1 -> new CrateEditProvider(filter, editing).open(player))
						.plugin(Nexus.getInstance())
						.open(player);
				}));

				// Display Item
				ItemStack displayItem = editing.getDisplayItemWithNull();
				if (displayItem == null)
					displayItem = new ItemBuilder(Material.ITEM_FRAME).name("&eDisplay Item")
						.lore("&3This will change into any item")
						.lore("&3click with it. It will be the")
						.lore("&3item that spawns on crate opening")
						.build();
				contents.set(0, 5, ClickableItem.of(displayItem, e -> {
					save(player.getOpenInventory().getTopInventory(), editing);
					InventoryClickEvent event = (InventoryClickEvent) e.getEvent();
					ItemStack display = isNullOrAir(event.getCursor()) ? null : event.getCursor();
					editing.setDisplayItem(display);
					editing.update();
					Tasks.wait(1, () -> {
						ItemStack item = event.getCursor();
						player.setItemOnCursor(null);
						new CrateEditProvider(filter, editing).open(player);
						player.setItemOnCursor(item);
					});
				}));

				// Toggle Active Item
				contents.set(0, 6, ClickableItem.of(new ItemBuilder(editing.isActive() ? Material.ENDER_CHEST : Material.CHEST)
					.name("&eToggle Active").lore("&3" + editing.isActive()).build(), e -> {
					save(player.getOpenInventory().getTopInventory(), editing);
					editing.setActive(!editing.isActive());
					editing.update();
					new CrateEditProvider(filter, editing).open(player);
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
						contents.setEditable(SlotPos.of(row, column), true);
			} else {
				// Close Item
				addCloseItem();

				// Filter Item
				contents.set(0, 8, ClickableItem.of(
					new ItemBuilder(Material.BOOK).name("&eFilter").lore("&3" + StringUtils.camelCase(filter.name())), e ->
						new CrateEditProvider(EnumUtils.nextWithLoop(CrateType.class, filter.ordinal()), null)
							.open(player)));

				// New Button
				contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK).name("&aCreate New").build(),
					e -> {
						CrateLoot loot = new CrateLoot(null, new ArrayList<>(), 20, filter, null);
						loot.setId(Crates.getNextId());
						loot.update();
						Crates.lootCache.add(loot);
						new CrateEditProvider(filter, loot).open(player);
					}));

				// Loot Items
				Pagination page = contents.pagination();
				List<ClickableItem> items = new ArrayList<>();
				Crates.getLootByType(filter).forEach(loot -> {
					ItemStack item = new ItemBuilder(loot.getDisplayItem() != null ? loot.getDisplayItem().getType() :
						(loot.isActive() ? Material.ENDER_CHEST : Material.CHEST))
						.name(loot.getTitle())
						.lore("&3Type: &e" + StringUtils.camelCase(loot.getType()))
						.lore(" ")
						.lore("&eLeft-Click &3to edit")
						.lore("&eRight-Click &3to enable/disable")
						.lore("&cShift-Click to Delete")
						.build();
					items.add(ClickableItem.of(item, e -> {
						InventoryClickEvent event = (InventoryClickEvent) e.getEvent();
						if (event.isShiftClick()) {
							ConfirmationMenu.builder()
								.title("Delete " + loot.getTitle() + "?")
								.onConfirm(e2 -> {
									loot.delete();
									new CrateEditProvider(filter, null).open(player);
								}).open(player);
							return;
						}
						if (event.isLeftClick()) {
							new CrateEditProvider(filter, loot).open(player);
							return;
						}
						if (event.isRightClick()) {
							loot.setActive(!loot.isActive());
							loot.update();
							new CrateEditProvider(filter, null).open(player, page.getPage());
						}
					}));
				});

				paginator().items(items).build();
			}
		}

		public void save(Inventory inventory, CrateLoot editing) {
			List<ItemStack> items = new ArrayList<>();
			ItemStack[] contents = inventory.getContents();
			for (int i = 9; i < 27; i++)
				if (!isNullOrAir(contents[i]))
					items.add(contents[i]);
			editing.setItems(items);
			editing.update();
		}

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent event) {
			Player player = (Player) event.getPlayer();
			SmartInventory inv = SmartInvsPlugin.manager().getInventory(player).orElse(null);
			if (inv == null) return;
			if (inv.getProvider() != this) return;
			CrateLoot editing = ((CrateEditProvider) inv.getProvider()).editing;
			if (editing == null) return;
			save(event.getInventory(), editing);
		}

	}


}
