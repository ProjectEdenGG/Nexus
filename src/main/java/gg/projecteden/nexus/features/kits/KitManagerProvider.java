package gg.projecteden.nexus.features.kits;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.RequiredArgsConstructor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.menus.MenuUtils.openAnvilMenu;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@RequiredArgsConstructor
public class KitManagerProvider extends InventoryProvider {
	private final Integer id;

	public KitManagerProvider() {
		this.id = null;
	}

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.title("Kit Manager")
			.provider(this)
			.rows(3)
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		if (id == null) {

			addCloseItem(contents);

			contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK).name("&aNew Kit").build(), e -> {
				int newId = KitManager.getNextId();
				KitManager.getConfig().set(newId + "", new Kit());
				KitManager.saveConfig();
				Tasks.wait(1, () -> new KitManagerProvider(newId).open(player));
			}));

			Kit[] kits = KitManager.getAllKits();

			Pagination page = contents.pagination();
			ClickableItem[] items = new ClickableItem[kits.length];

			for (int i = 0; i < kits.length; i++) {
				Kit kit = kits[i];
				ItemStack item = new ItemBuilder(Material.CHEST).name("&e" + StringUtils.camelCase(kit.getName())).lore(" ").lore("&7Shift-Right Click to delete").build();
				items[i] = ClickableItem.of(item, e -> {
					if (((InventoryClickEvent) e.getEvent()).isRightClick() && ((InventoryClickEvent) e.getEvent()).isShiftClick()) {
						ConfirmationMenu.builder().onCancel(itemClickData -> new KitManagerProvider().open(player))
								.onConfirm(itemClickData -> {
									KitManager.getConfig().set(kit.getId() + "", null);
									KitManager.saveConfig();
									Tasks.wait(1, () -> new KitManagerProvider().open(player));
								}).open(player);
					} else
						new KitManagerProvider(kit.getId()).open(player);
				});
			}

			page.setItems(items);
			page.setItemsPerPage(9);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isFirst())
				contents.set(2, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).name("<-- Back").build(),
					e -> new KitManagerProvider().open(player, page.previous().getPage())));
			if (!page.isLast())
				contents.set(2, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).name("Next -->").build(),
					e -> new KitManagerProvider().open(player, page.next().getPage())));

		} else {
			addBackItem(contents, e -> {
				saveItems(player);
				new KitManagerProvider().open(player);
			});

			contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.BOOK).name("&e" + KitManager.get(id).getName()).lore("&3Click to set the").lore("&3name of the kit").build(), e -> {
				Kit kit = KitManager.get(id);
				openAnvilMenu(player, kit.getName(), (player1, response) -> {
					kit.setName(response);
					KitManager.getConfig().set(id + "", kit);
					KitManager.saveConfig();
					Tasks.wait(1, () -> new KitManagerProvider(id).open(player));
					return AnvilGUI.Response.text(response);
				}, player1 -> new KitManagerProvider(id).open(player));
			}));

			contents.set(0, 5, ClickableItem.of(new ItemBuilder(Material.CLOCK).name("&eDelay").lore("&e" + KitManager.get(id).getDelay())
					.lore("&e(" + Timespan.ofSeconds(KitManager.get(id).getDelay() / 20).format() + ")").build(), e -> {
				Kit kit = KitManager.get(id);
				openAnvilMenu(player, "" + kit.getDelay(), (player1, response) -> {
					try {
						kit.setDelay(Integer.parseInt(response));
					} catch (Exception ex) {
						PlayerUtils.send(player, "&cDelay must be a number written in ticks less than " + Integer.MAX_VALUE +
								" (" + Timespan.ofSeconds(Integer.MAX_VALUE / 20).format() + ")");
						return AnvilGUI.Response.close();
					}
					KitManager.getConfig().set(id + "", kit);
					KitManager.saveConfig();
					Tasks.wait(1, () -> new KitManagerProvider(id).open(player));
					return AnvilGUI.Response.text(response);
				}, player1 -> new KitManagerProvider(id).open(player));
			}));

			contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.END_CRYSTAL).name("&eSave").build(), e -> saveItems(player)));

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
					contents.setEditable(SlotPos.of(i, j), true);
		}
	}

	public void saveItems(Player player) {
		int[] editableSlots = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
		Inventory inv = player.getOpenInventory().getTopInventory();
		List<ItemStack> items = new ArrayList<>();
		for (int i : editableSlots) {
			if (isNullOrAir(inv.getItem(i))) continue;
			items.add(inv.getItem(i));
		}
		Kit kit = KitManager.get(id);
		kit.setItems(items.toArray(ItemStack[]::new));
		KitManager.getConfig().set(id + "", kit);
		KitManager.saveConfig();
	}
}
