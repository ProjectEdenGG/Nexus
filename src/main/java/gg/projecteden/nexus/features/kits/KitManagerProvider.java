package gg.projecteden.nexus.features.kits;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Kit Manager")
@Rows(3)
@RequiredArgsConstructor
public class KitManagerProvider extends InventoryProvider {
	private final Integer id;

	public KitManagerProvider() {
		this.id = null;
	}

	@Override
	public void init() {
		if (id == null) {
			addCloseItem();

			contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK).name("&aNew Kit").build(), e -> {
				int newId = KitManager.getNextId();
				KitManager.getConfig().set(newId + "", new Kit());
				KitManager.saveConfig();
				Tasks.wait(1, () -> new KitManagerProvider(newId).open(viewer));
			}));

			Kit[] kits = KitManager.getAllKits();

			List<ClickableItem> items = new ArrayList<>();

			for (Kit kit : kits) {
				ItemStack item = new ItemBuilder(Material.CHEST).name("&e" + StringUtils.camelCase(kit.getName())).lore(" ").lore("&7Shift-Right Click to delete").build();
				items.add(ClickableItem.of(item, e -> {
					if (e.isShiftClick())
						ConfirmationMenu.builder().onCancel(itemClickData -> new KitManagerProvider().open(viewer))
							.onConfirm(itemClickData -> {
								KitManager.getConfig().set(kit.getId() + "", null);
								KitManager.saveConfig();
								Tasks.wait(1, () -> new KitManagerProvider().open(viewer));
							}).open(viewer);
					else
						new KitManagerProvider(kit.getId()).open(viewer);
				}));
			}

			paginator().items(items).perPage(9).build();
		} else {
			addBackItem(e -> {
				saveItems(viewer);
				new KitManagerProvider().open(viewer);
			});

			contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.BOOK).name("&e" + KitManager.get(id).getName()).lore("&3Click to set the").lore("&3name of the kit").build(), e -> {
				Kit kit = KitManager.get(id);
				MenuUtils.openAnvilMenu(viewer, kit.getName(), (player1, response) -> {
					kit.setName(response);
					KitManager.getConfig().set(id + "", kit);
					KitManager.saveConfig();
					Tasks.wait(1, () -> new KitManagerProvider(id).open(viewer));
					return AnvilGUI.Response.text(response);
				}, player1 -> new KitManagerProvider(id).open(viewer));
			}));

			contents.set(0, 5, ClickableItem.of(new ItemBuilder(Material.CLOCK).name("&eDelay").lore("&e" + KitManager.get(id).getDelay())
					.lore("&e(" + Timespan.ofSeconds(KitManager.get(id).getDelay() / 20).format() + ")").build(), e -> {
				Kit kit = KitManager.get(id);
				MenuUtils.openAnvilMenu(viewer, "" + kit.getDelay(), (player1, response) -> {
					try {
						kit.setDelay(Integer.parseInt(response));
					} catch (Exception ex) {
						PlayerUtils.send(viewer, "&cDelay must be a number written in ticks less than " + Integer.MAX_VALUE +
							" (" + Timespan.ofSeconds(Integer.MAX_VALUE / 20).format() + ")");
						return AnvilGUI.Response.close();
					}
					KitManager.getConfig().set(id + "", kit);
					KitManager.saveConfig();
					Tasks.wait(1, () -> new KitManagerProvider(id).open(viewer));
					return AnvilGUI.Response.text(response);
				}, player1 -> new KitManagerProvider(id).open(viewer));
			}));

			contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.END_CRYSTAL).name("&eSave").build(), e -> saveItems(viewer)));

			int row = 1;
			int column = 0;

			for (ItemStack item : KitManager.get(id).getItems()) {
				contents.set(row, column, ClickableItem.empty(item));

				if (column == 8) {
					column = 0;
					row++;
				} else
					column++;
			}

			for (int i = 1; i <= 2; i++)
				for (int j = 0; j <= 8; j++)
					contents.setEditable(i, j, true);
		}
	}

	public void saveItems(Player player) {
		int[] editableSlots = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
		Inventory inv = player.getOpenInventory().getTopInventory();
		List<ItemStack> items = new ArrayList<>();
		for (int i : editableSlots) {
			if (Nullables.isNullOrAir(inv.getItem(i))) continue;
			items.add(inv.getItem(i));
		}
		Kit kit = KitManager.get(id);
		kit.setItems(items.toArray(ItemStack[]::new));
		KitManager.getConfig().set(id + "", kit);
		KitManager.saveConfig();
	}
}
