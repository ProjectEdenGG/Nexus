package me.pugabyte.bncore.features.holidays.pugmas20;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.pugmas20.menu.AdventMenu;
import me.pugabyte.bncore.features.holidays.pugmas20.models.AdventChest;
import me.pugabyte.bncore.features.holidays.pugmas20.models.AdventChest.District;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.SoundUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.ActionGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20.pugmasLoc;

// TODO PUGMAS - Prevent adventLootHead from being placed, or opened if player doesn't have enough space
public class AdventChests implements Listener {
	public static Map<Integer, Location> adventLootMap = new HashMap<>();
	public static List<AdventChest> adventChestList = new ArrayList<>();
	public static ItemBuilder adventLootHead;
	//
	//TODO PUGMAS - Change to final location
	private static final Block lootOrigin = pugmasLoc(-959, 10, -2090).getBlock();
	private static final String InvTitle = "Advent Chest #";
	private static UUID adventLootHeadOwner = null;
	private static final String adventLootHeadTitle = "Pugmas Advent Skull";
	private static final String adventLootHeadLore = "Day #";

	public AdventChests() {
		BNCore.registerListener(this);

		AdventMenu.origin.getRelative(0, 0, 1).getDrops().stream()
				.findFirst().ifPresent(skull -> adventLootHead = new ItemBuilder(skull));
		adventLootHeadOwner = Utils.getSkullOwner(adventLootHead.build());

		loadLootLocations();
		loadChestLocations();
	}

	private void loadLootLocations() {
		int index = 1;
		for (int z = 0; z <= 6; z++) {         // 0-3 col (Every other)
			for (int x = 0; x <= 12; x++) {    // 0-6 row (Every other)
				Block block = lootOrigin.getRelative(x, 0, z);
				if (Utils.isNullOrAir(block.getType()) || !block.getType().equals(Material.CHEST))
					continue;

				adventLootMap.put(index++, block.getLocation());
			}
		}
	}

	private void loadChestLocations() {
		adventChestList.add(new AdventChest(1, chestLoc(-959, 11, -2081), District.UNKNOWN));
		// TODO PUGMAS - Add all chest locations
	}

	private Location chestLoc(int x, int y, int z) {
		return pugmasLoc(x, y, z);
	}

	public static AdventChest getAdventChest(Location location) {
		return adventChestList.stream().filter(adventChest -> adventChest.getLocation().equals(location)).findFirst().orElse(null);
	}

	public static AdventChest getAdventChest(int day) {
		return adventChestList.stream().filter(adventChest -> adventChest.getDay() == day).findFirst().orElse(null);
	}

	// Advent Methods & Listeners

	@EventHandler
	public void onAdventChestOpen(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (Utils.isNullOrAir(event.getClickedBlock())) return;
		if (!Pugmas20.isAtPugmas(event.getPlayer())) return;

		Block block = event.getClickedBlock();
		if (!block.getType().equals(Material.CHEST)) return;

		AdventChest adventChest = getAdventChest(block.getLocation());
		if (adventChest == null) return;

		event.setCancelled(true);
		int chestDay = adventChest.getDay();

		// TODO PUGMAS:
		//  - verify that the player can open this chest
		//  - verify that the player has at least 1 slot free
		//  - save which chest was opened to pugmas20 user
//		int day = LocalDate.now().getDayOfMonth();

		giveAdventHead(event.getPlayer(), chestDay);
	}

	public static void giveAdventHead(Player player, int day) {
		// TODO PUGMAS: "item get" sound?
		ItemStack skull = adventLootHead.clone().name(adventLootHeadTitle).lore(adventLootHeadLore + day).build();

		Inventory inventory = Bukkit.createInventory(null, 3 * 9, InvTitle + day);
		inventory.setItem(13, skull);
		player.openInventory(inventory);
		SoundUtils.playSound(player, Sound.BLOCK_CHEST_OPEN);
	}

	@EventHandler
	public void onOpenAdventLootHead(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;

		ItemStack skull = Utils.getTool(player);
		if (Utils.isNullOrAir(skull) || !skull.getType().equals(Material.PLAYER_HEAD)) return;
		if (Utils.isNullOrEmpty(skull.getLore())) return;

		UUID skullOwner = Utils.getSkullOwner(skull);
		if (skullOwner == null) return;
		if (!skullOwner.equals(adventLootHeadOwner)) return;

		event.setCancelled(true);
		String lore = skull.getLore().get(0);
		int day = Integer.parseInt(lore.replaceAll(adventLootHeadLore, ""));

		if (openAdventLootInv(player, day))
			skull.setAmount(skull.getAmount() - 1);
	}

	public static boolean openAdventLootInv(Player player, int day) {
		// TODO PUGMAS: verify that the player has space for contents, if not, return false
		Inventory inventory = Bukkit.createInventory(null, 3 * 9, InvTitle + day);

		Location loc = AdventChests.adventLootMap.get(day);
		if (loc == null)
			return false;

		Chest chest = (Chest) loc.getBlock().getState();
		inventory.setContents(chest.getBlockInventory().getContents());

		player.openInventory(inventory);
		SoundUtils.playSound(player, Sound.BLOCK_CHEST_OPEN);

		return true;
	}

	@EventHandler
	public void onAdventLootInvClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.containsInvViewTitle(event.getView(), InvTitle)) return;

		List<ItemStack> leftover = new ArrayList<>(Arrays.asList(event.getInventory().getContents())).stream()
				.filter(itemStack -> !Utils.isNullOrAir(itemStack)).collect(Collectors.toList());

		if (leftover.size() == 0)
			return;

		Utils.send(player, "Giving leftover items...");
		Utils.giveItems(player, leftover);
	}


}
