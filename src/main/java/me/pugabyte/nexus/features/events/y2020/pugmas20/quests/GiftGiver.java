package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.AdventChests;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@NoArgsConstructor
public class GiftGiver implements Listener {
	private static final String error = Pugmas20.getPREFIX() + "&cYou can't do that with this item";
	private static final String locked = Pugmas20.getPREFIX() + "&cYou can't open this gift!";
	private static final ItemStack skull = AdventMenu.origin.getRelative(0, 0, 5).getDrops().stream().findFirst().orElse(null);
	private static ItemStack gift_locked = null;
	private static ItemStack gift_unlocked = null;
	private static List<Location> lootChestList = new ArrayList<>();
	private static final String invTitle = "Pugmas20 Gift";

	static {
		if (skull != null) {
			gift_locked = new ItemBuilder(skull.clone()).name(camelCase("Gift"))
					.lore(Pugmas20.getItemLore(), "&f", "&eRight Click another player while holding to give it to them")
					.build();
			gift_unlocked = new ItemBuilder(skull.clone()).name(camelCase("Gift"))
					.lore(Pugmas20.getItemLore(), "&f", "&aLeft Click to open", "&eRClick another player while holding to give it to them")
					.build();
		}

		for (int i = 0; i < 9; i++) {
			Block block = AdventChests.lootOrigin.getRelative(-3, 0, i);
			if (ItemUtils.isNullOrAir(block.getType()) || !block.getType().equals(Material.CHEST))
				continue;
			lootChestList.add(block.getLocation());
		}
	}

	public static void giveGift(Player player) {
		if (gift_locked == null || gift_unlocked == null) return;
		ItemUtils.giveItem(player, gift_locked);
		Quests.sound_obtainItem(player);
	}

	public static void tradeGift(Player from, Player to, ItemStack gift) {
		if (!Quests.hasRoomFor(to, gift)) {
			Utils.send(from, Pugmas20.getPREFIX() + "&cCannot give gift to " + to.getName() + ", their inventory is full!");
			Quests.sound_villagerNo(from);
			return;
		}

		Utils.send(from, Pugmas20.getPREFIX() + "You gave your gift to " + to.getName());
		from.getInventory().removeItem(gift);
		Quests.sound_obtainItem(from);

		Utils.send(to, Pugmas20.getPREFIX() + from.getName() + " gave you a gift!");
		ItemUtils.giveItem(to, gift_unlocked);
		Quests.sound_obtainItem(from);
	}

	public static void openGift(Player player, ItemStack gift) {
		if (!ItemUtils.isFuzzyMatch(gift_unlocked, gift)) {
			Utils.send(player, locked);
			Quests.sound_villagerNo(player);
			return;
		}

		Location loc = RandomUtils.randomElement(lootChestList);
		Chest chest = (Chest) loc.getBlock().getState();
		ItemStack[] contents = chest.getBlockInventory().getContents();
		if (!Quests.hasRoomFor(player, contents)) {
			Utils.send(player, Quests.fullInvError);
			Quests.sound_villagerNo(player);
			return;
		}

		Inventory inventory = Bukkit.createInventory(null, 3 * 9, invTitle);
		inventory.setContents(contents);

		player.openInventory(inventory);
		SoundUtils.playSound(player, Sound.BLOCK_CHEST_OPEN);
		player.getInventory().removeItem(gift);
	}

	@EventHandler
	public void onAdventLootInvClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.containsInvViewTitle(event.getView(), invTitle)) return;

		List<ItemStack> leftover = new ArrayList<>(Arrays.asList(event.getInventory().getContents())).stream()
				.filter(itemStack -> !ItemUtils.isNullOrAir(itemStack)).collect(Collectors.toList());

		if (leftover.size() == 0)
			return;

		Utils.send(player, Quests.leftoverItems);
		ItemUtils.giveItems(player, leftover);
	}

	@EventHandler
	public void onPlayerTradeGift(PlayerInteractEntityEvent event) {
		if (!event.getRightClicked().getType().equals(EntityType.PLAYER)) return;
		if (CitizensUtils.isNPC(event.getRightClicked())) return;

		Player clicker = event.getPlayer();
		Player clicked = (Player) event.getRightClicked();
		ItemStack gift = ItemUtils.getTool(clicker);
		if (ItemUtils.isNullOrAir(gift)) return;
		if (gift_locked == null || gift_unlocked == null) return;
		if (!ItemUtils.isFuzzyMatch(gift, gift_locked) && !ItemUtils.isFuzzyMatch(gift, gift_unlocked)) return;

		event.setCancelled(true);
		tradeGift(clicker, clicked, gift);
	}

	@EventHandler
	public void onInteractWithGift(PlayerInteractEvent event) {
		ItemStack gift = ItemUtils.getTool(event.getPlayer());
		if (ItemUtils.isNullOrAir(gift)) return;
		if (gift_locked == null || gift_unlocked == null) return;
		if (!ItemUtils.isFuzzyMatch(gift, gift_locked) && !ItemUtils.isFuzzyMatch(gift, gift_unlocked)) return;

		event.setCancelled(true);
		if (ActionGroup.LEFT_CLICK.applies(event)) {
			openGift(event.getPlayer(), gift);
		} else
			Utils.send(event.getPlayer(), error);
	}

	@EventHandler
	public void onDropGift(PlayerDropItemEvent event) {
		ItemStack gift = event.getItemDrop().getItemStack();
		if (ItemUtils.isNullOrAir(gift)) return;
		if (gift_locked == null || gift_unlocked == null) return;
		if (!ItemUtils.isFuzzyMatch(gift, gift_locked) && !ItemUtils.isFuzzyMatch(gift, gift_unlocked)) return;

		event.setCancelled(true);
		Utils.send(event.getPlayer(), error);
	}
}
