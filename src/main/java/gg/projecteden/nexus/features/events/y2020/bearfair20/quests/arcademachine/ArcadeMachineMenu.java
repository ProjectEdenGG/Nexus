package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.arcademachine;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.MinigameNightIsland;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.annotations.Uncloseable;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@Rows(5)
@Uncloseable
@Title("&3Arcade Machine")
@NoArgsConstructor
public class ArcadeMachineMenu extends InventoryProvider implements Listener {
	private ItemStack[] items;
	private static final int[] openSlots = {0, 2, 4, 8, 20, 22, 26, 38, 44};
	private static final Material[] correct = {Material.IRON_TRAPDOOR, Material.DAYLIGHT_DETECTOR, Material.IRON_INGOT, Material.NOTE_BLOCK,
		Material.GREEN_CARPET, Material.REPEATER, Material.HOPPER_MINECART, Material.BLAST_FURNACE, Material.LEVER};
	private static final int[][] wireGroups = {{9, 18, 19}, {11}, {13}, {6, 7, 15}, {29}, {21}, {23, 24, 25}, {37}, {33, 42, 43}};

	@Override
	public void init() {
		contents.set(40, ClickableItem.of(closeItem(), e -> {
			for (int i : openSlots) {
				if (!contents.get(i).get().getItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
					PlayerUtils.giveItem(viewer, contents.get(i).get().getItem());
			}
			viewer.closeInventory();
		}));

		int[] blackSlots = {1, 3, 5, 10, 12, 14, 16, 17, 27, 28, 30, 31, 32, 34, 35, 39, 41};
		for (int i : blackSlots)
			contents.set(i, ClickableItem.empty(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build()));

		contents.set(36, ClickableItem.of(new ItemBuilder(Material.STONE_BUTTON).name("Power Button").build(), e -> {
			process(contents);
			boolean complete = true;
			for (int i = 0; i < openSlots.length; i++) {
				if (!contents.get(openSlots[i]).get().getItem().getType().equals(correct[i]))
					complete = false;
				else if (contents.get(openSlots[i]).get().getItem().getLore() != null
						&& !contents.get(openSlots[i]).get().getItem().getLore().contains(BFQuests.itemLore))
					complete = false;
			}
			if (!complete)
				viewer.playSound(viewer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.1f);
			else {
				complete(viewer);
			}
		}));

		for (int i = 0; i < items.length; i++) {
			ItemStack item = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(" ").build();
			if (items[i] != null) {
				item = items[i];
				for (int j : wireGroups[i])
					contents.set(j, ClickableItem.empty(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name(" ").build()));
			}
			contents.set(openSlots[i], ClickableItem.of(item, e -> {
				if (e.getItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {
					if (Nullables.isNullOrAir(viewer.getItemOnCursor())) return;
					contents.set(e.getSlot(), ClickableItem.empty(viewer.getItemOnCursor()));
					viewer.setItemOnCursor(null);
					getItems(viewer, contents);
				} else {
					if (Nullables.isNullOrAir(viewer.getItemOnCursor()))
						contents.set(e.getSlot(), ClickableItem.empty(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(" ").build()));
					else {
						contents.set(e.getSlot(), ClickableItem.empty(viewer.getItemOnCursor()));
						viewer.setItemOnCursor(null);
					}
					getItems(viewer, contents);
					viewer.setItemOnCursor(e.getItem());
				}
			}));
		}

		for (int i = 0; i < wireGroups.length; i++)
			for (int j : wireGroups[i])
				if (contents.get(openSlots[i]).get().getItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
					contents.set(j, ClickableItem.empty(new ItemBuilder(Material.GLASS_PANE).name(" ").build()));

	}

	public void complete(Player player) {
		player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
		Tasks.wait(TickTime.SECOND.x(5), player::closeInventory);

		MinigameNightIsland.nextStep(player); // 2
	}

	public void getItems(Player player, InventoryContents contents) {
		ItemStack[] items = new ItemStack[openSlots.length];
		for (int i = 0; i < openSlots.length; i++) {
			ItemStack item = contents.get(openSlots[i]).get().getItem();
			if (item.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
				items[i] = null;
			else
				items[i] = item;
		}
		this.items = items;
		open(player);
	}

	public void process(InventoryContents contents) {
		for (int i = 0; i < wireGroups.length; i++) {
			for (int j : wireGroups[i]) {
				if (contents.get(openSlots[i]).get().getItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
					contents.set(j, ClickableItem.empty(new ItemBuilder(Material.GLASS_PANE).name(" ").build()));
				else if (contents.get(openSlots[i]).get().getItem().getType().equals(correct[i]) &&
						contents.get(openSlots[i]).get().getItem().getLore() != null &&
						contents.get(openSlots[i]).get().getItem().getLore().contains(BFQuests.itemLore))
					contents.set(j, ClickableItem.empty(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name(" ").build()));
				else
					contents.set(j, ClickableItem.empty(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(" ").build()));
			}
		}
	}
}
