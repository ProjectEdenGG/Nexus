package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.arcademachine;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.MinigameNightIsland;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ArcadeMachineMenu extends MenuUtils implements InventoryProvider, Listener {

	public SmartInventory getInv(ItemStack[] items) {
		return SmartInventory.builder()
				.title(StringUtils.colorize("&3Arcade Machine"))
				.provider(new ArcadeMachineMenu(items))
				.size(5, 9)
				.closeable(false)
				.build();
	}

	public void open(Player player, ItemStack[] items) {
		getInv(items).open(player);
	}

	public void close(Player player, ItemStack[] items) {
		getInv(items).close(player);
	}

	ItemStack[] items;
	int[] openSlots = {0, 2, 4, 8, 20, 22, 26, 38, 44};
	Material[] correct = {Material.IRON_TRAPDOOR, Material.DAYLIGHT_DETECTOR, Material.IRON_INGOT, Material.NOTE_BLOCK,
			Material.GREEN_CARPET, Material.REPEATER, Material.HOPPER_MINECART, Material.BLAST_FURNACE, Material.LEVER};
	int[][] wireGroups = {{9, 18, 19}, {11}, {13}, {6, 7, 15}, {29}, {21}, {23, 24, 25}, {37}, {33, 42, 43}};

	public ArcadeMachineMenu() {
	}

	public ArcadeMachineMenu(ItemStack... items) {
		this.items = items;
		if (items == null || items.length < openSlots.length)
			this.items = new ItemStack[openSlots.length];
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(40, ClickableItem.from(closeItem(), e -> {
			for (int i : openSlots) {
				if (!contents.get(i).get().getItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
					PlayerUtils.giveItem(player, contents.get(i).get().getItem());
			}
			close(player, items);
		}));

		int[] blackSlots = {1, 3, 5, 10, 12, 14, 16, 17, 27, 28, 30, 31, 32, 34, 35, 39, 41};
		for (int i : blackSlots)
			contents.set(i, ClickableItem.empty(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build()));

		contents.set(36, ClickableItem.from(new ItemBuilder(Material.STONE_BUTTON).name("Power Button").build(), e -> {
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
				player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.1f);
			else {
				complete(player);
			}
		}));

		for (int i = 0; i < items.length; i++) {
			ItemStack item = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(" ").build();
			if (items[i] != null) {
				item = items[i];
				for (int j : wireGroups[i])
					contents.set(j, ClickableItem.empty(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name(" ").build()));
			}
			contents.set(openSlots[i], ClickableItem.from(item, e -> {
				if (e.getItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {
					if (Nullables.isNullOrAir(player.getItemOnCursor())) return;
					contents.set(e.getSlot(), ClickableItem.empty(player.getItemOnCursor()));
					player.setItemOnCursor(null);
					getItems(player, contents);
				} else {
					if (Nullables.isNullOrAir(player.getItemOnCursor()))
						contents.set(e.getSlot(), ClickableItem.empty(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(" ").build()));
					else {
						contents.set(e.getSlot(), ClickableItem.empty(player.getItemOnCursor()));
						player.setItemOnCursor(null);
					}
					getItems(player, contents);
					player.setItemOnCursor(e.getItem());
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
		Tasks.wait(TickTime.SECOND.x(5), () -> close(player, items));

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
		open(player, items);
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
