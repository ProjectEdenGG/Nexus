package me.pugabyte.nexus.features.events.y2020.pugmas20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeavingEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.AdventChest;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.AdventChest.District;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Quests;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import me.pugabyte.nexus.utils.WorldGuardUtils;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isBeforePugmas;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isPastPugmas;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isSecondChance;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.location;

// TODO PUGMAS - Prevent adventLootHead from being placed, or opened if player doesn't have enough space
public class AdventChests implements Listener {
	public static Map<Integer, Location> adventLootMap = new HashMap<>();
	public static List<AdventChest> adventChestList = new ArrayList<>();
	public static ItemBuilder adventLootHead;
	//
	public static final Block lootOrigin = location(867, 45, 579).getBlock();
	private static final String InvTitle = "Advent Chest #";
	private static UUID adventLootHeadOwner = null;
	private static final String adventLootHeadTitle = "Pugmas Advent Skull";
	private static final String adventLootHeadLore = "Day #";
	private static final String districtRg = Pugmas20.getRegion() + "_district_";
	//
	private static final String wrongDay = Pugmas20.getPREFIX() + "You cannot open this chest, look for chest #<day>";
	private static final String openPrevious = Pugmas20.getPREFIX() + "need to find the rest to open this one";
	private static final String alreadyFound = Pugmas20.getPREFIX() + "already opened this chest!";

	public AdventChests() {
		Nexus.registerListener(this);

		AdventMenu.origin.getRelative(0, 0, 1).getDrops().stream()
				.findFirst().ifPresent(skull -> adventLootHead = new ItemBuilder(skull));
		adventLootHeadOwner = ItemUtils.getSkullOwner(adventLootHead.build());

		loadLootLocations();
		loadChestLocations();
	}

	private void loadLootLocations() {
		int index = 1;
		for (int z = 0; z <= 6; z++) {         // 0-3 col (Every other)
			for (int x = 0; x <= 12; x++) {    // 0-6 row (Every other)
				Block block = lootOrigin.getRelative(x, 0, z);
				if (ItemUtils.isNullOrAir(block.getType()) || !block.getType().equals(Material.CHEST))
					continue;

				adventLootMap.put(index++, block.getLocation());
			}
		}
	}

	private void loadChestLocations() {
		adventChestList.add(new AdventChest(1, chestLoc(867, 46, 588)));
		// TODO PUGMAS - Add all chest locations
	}

	private Location chestLoc(int x, int y, int z) {
		return location(x, y, z);
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
		if (BlockUtils.isNullOrAir(event.getClickedBlock())) return;

		Player player = event.getPlayer();
		if (!Pugmas20.isAtPugmas(player)) return;

		Block block = event.getClickedBlock();
		if (!block.getType().equals(Material.CHEST)) return;

		AdventChest adventChest = getAdventChest(block.getLocation());
		if (adventChest == null) return;

		if (!Quests.hasRoomFor(player, 1)) {
			Utils.send(player, Quests.fullInvError);
			return;
		}

		event.setCancelled(true);

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);

		int chestDay = adventChest.getDay();
		LocalDateTime now = LocalDateTime.now();
		int day = LocalDate.now().getDayOfMonth();

		if (isBeforePugmas(now)) return;
		if (isPastPugmas(now)) return;

		boolean openChest = false;
		String reason = "";
		if (user.getFoundDays().contains(chestDay))
			reason = alreadyFound;
		else if (isSecondChance(now))
			if (chestDay != 25 || user.getFoundDays().size() == 24)
				openChest = true;
			else
				reason = openPrevious;
		else if (chestDay == day)
			openChest = true;
		else
			reason = wrongDay;

		if (!openChest) {
			reason = reason.replaceAll("<day>", String.valueOf(day));
			Utils.send(player, reason);
			return;
		}

		// TODO PUGMAS:
		//  - verify that the player can open this chest
		//  - save which chest was opened to pugmas20 user

		user.getFoundDays().add(chestDay);
		service.save(user);

		giveAdventHead(player, chestDay);
	}

	public static void giveAdventHead(Player player, int day) {
		ItemStack skull = adventLootHead.clone().name(adventLootHeadTitle).lore(adventLootHeadLore + day).build();

		Inventory inventory = Bukkit.createInventory(null, 3 * 9, InvTitle + day);
		inventory.setItem(13, skull);
		player.openInventory(inventory);
		SoundUtils.playSound(player, Sound.BLOCK_CHEST_OPEN);
	}

	public static District getDistrict(Location location) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(location);
		District district = null;
		for (ProtectedRegion region : WGUtils.getRegionsAt(location)) {
			if (region.getId().contains(districtRg)) {
				district = District.valueOf(region.getId().replace(districtRg, "").toUpperCase());
			}
		}

		if (district == null)
			district = District.UNKNOWN;

		return district;
	}

	@EventHandler
	public void onOpenAdventLootHead(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;

		ItemStack skull = ItemUtils.getTool(player);
		if (ItemUtils.isNullOrAir(skull) || !skull.getType().equals(Material.PLAYER_HEAD)) return;
		if (Utils.isNullOrEmpty(skull.getLore())) return;

		UUID skullOwner = ItemUtils.getSkullOwner(skull);
		if (skullOwner == null) return;
		if (!skullOwner.equals(adventLootHeadOwner)) return;

		String lore = skull.getLore().get(0);
		if(!lore.contains(adventLootHeadLore)) return;

		event.setCancelled(true);
		int day = Integer.parseInt(lore.replaceAll(adventLootHeadLore, ""));

		if (openAdventLootInv(player, day))
			skull.setAmount(skull.getAmount() - 1);
	}

	public static boolean openAdventLootInv(Player player, int day) {
		Inventory inventory = Bukkit.createInventory(null, 3 * 9, InvTitle + day);

		Location loc = AdventChests.adventLootMap.get(day);
		if (loc == null)
			return false;

		Chest chest = (Chest) loc.getBlock().getState();
		ItemStack[] contents = chest.getBlockInventory().getContents();
		if (!Quests.hasRoomFor(player, contents)) {
			Utils.send(player, Quests.fullInvError);
			Quests.sound_villagerNo(player);
			return false;
		}

		inventory.setContents(contents);

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
				.filter(itemStack -> !ItemUtils.isNullOrAir(itemStack)).collect(Collectors.toList());

		if (leftover.size() == 0)
			return;

		Utils.send(player, Quests.leftoverItems);
		ItemUtils.giveItems(player, leftover);
	}

	@EventHandler
	public void onDistrictEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas20.isAtPugmas(player)) return;

		Location loc = player.getLocation();
		if (Pugmas20.WGUtils.getRegionsLikeAt(districtRg + ".*", loc).size() == 0) return;

		District district = getDistrict(loc);
		if (district != District.UNKNOWN)
			ActionBarUtils.sendActionBar(player, "&a&lEntering " + district.getName() + " District");
	}

	@EventHandler
	public void onDistrictExit(RegionLeavingEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas20.isAtPugmas(player)) return;

		Location loc = player.getLocation();
		if (Pugmas20.WGUtils.getRegionsLikeAt(districtRg + ".*", loc).size() == 0) return;

		District district = getDistrict(loc);
		if (district != District.UNKNOWN)
			ActionBarUtils.sendActionBar(player, "&c&lExiting " + district.getName() + " District");
	}


}
