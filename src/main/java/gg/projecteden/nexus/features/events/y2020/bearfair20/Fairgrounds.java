package gg.projecteden.nexus.features.events.y2020.bearfair20;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2020.bearfair20.fairgrounds.*;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Fairgrounds implements Listener {

	public Fairgrounds() {
		Nexus.registerListener(this);
		new Timer("        BF20.Fairgrounds.PugDunk", PugDunk::new);
		new Timer("        BF20.Fairgrounds.Archery", Archery::new);
		new Timer("        BF20.Fairgrounds.Frogger", Frogger::new);
		new Timer("        BF20.Fairgrounds.Basketball", Basketball::new);
		new Timer("        BF20.Fairgrounds.Reflection", Reflection::new);
		new Timer("        BF20.Fairgrounds.Interactables", Interactables::new);
	}

	public static void giveKit(BearFairKit kit, Player player) {
		if (slotsTaken(player) <= (36 - kit.items.size())) {
			PlayerUtils.giveItems(player, kit.items);
		}
	}

	private static int slotsTaken(Player player) {
		ItemStack[] items = player.getInventory().getContents();
		int count = 0;
		for (ItemStack item : items) {
			if (item == null || Nullables.isNullOrAir(item.getType())) continue;
			count++;
		}
		return count;
	}

	public static void removeKits(Player player) {
		List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
		for (ItemStack item : items) {
			if (!BearFair20.isBFItem(item)) continue;

			if (isBFKitItem(item))
				player.getInventory().remove(item);
		}

		ItemStack offHand = player.getInventory().getItemInOffHand();
		if (isBFKitItem(offHand))
			player.getInventory().setItemInOffHand(null);
	}

	private static boolean isBFKitItem(ItemStack item) {
		ItemStack oneItem = item.clone();
		oneItem.setAmount(1);
		for (BearFairKit kit : BearFairKit.values()) {
			if (kit.getItems().contains(oneItem))
				return true;
		}
		return false;
	}

	public enum BearFairKit {
		BOW_AND_ARROW(
				new ItemBuilder(Material.BOW)
						.enchant(Enchantment.INFINITY)
						.lore(BFQuests.itemLore)
						.unbreakable()
						.build(),
				new ItemBuilder(Material.ARROW)
						.lore(BFQuests.itemLore)
						.build()
		),
		MINECART(
				new ItemBuilder(Material.MINECART)
						.lore(BFQuests.itemLore)
						.build()
		),
		BASKETBALL(
				getBasketball()
		);

		List<ItemStack> items;

		BearFairKit(ItemStack... items) {
			this.items = Arrays.asList(items);
		}

		public ItemStack getItem() {
			return getItems().get(0);
		}

		public List<ItemStack> getItems() {
			return items;
		}

	}

	private static ItemStack getBasketball() {
		ItemStack basketballConfig = (ItemStack) Nexus.getInstance().getConfig().get("minigames.lobby.basketball.item");
		if (basketballConfig == null)
			basketballConfig = new ItemStack(Material.SKELETON_SKULL);

		ItemStack basketball = basketballConfig.clone();

		ItemMeta meta = basketball.getItemMeta();
		meta.setLore(Collections.singletonList(StringUtils.colorize("&eBearFair20 Basketball")));
		meta.setDisplayName(StringUtils.colorize("&6&lBasketball"));
		basketball.setItemMeta(meta);

		return basketball;
	}

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		String id = event.getRegion().getId();
		if (id.contains(BearFair20.getRegion() + "_bow_"))
			giveKit(BearFairKit.BOW_AND_ARROW, event.getPlayer());
		if (id.contains(BearFair20.getRegion() + "_minecart_"))
			giveKit(BearFairKit.MINECART, event.getPlayer());
	}

	@EventHandler
	public void onRegionExit(PlayerLeftRegionEvent event) {
		String id = event.getRegion().getId();
		String bowRg = BearFair20.getRegion() + "_bow_";
		String minecartRg = BearFair20.getRegion() + "_minecart_";
		if (id.contains(bowRg) || id.contains(minecartRg) || id.contains(BearFair20.getRegion())) {
			removeKits(event.getPlayer());
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		ProtectedRegion region = BearFair20.worldguard().getProtectedRegion(BearFair20.getRegion());
		if (BearFair20.worldguard().getRegionsAt(event.getPlayer().getLocation()).contains(region)) {
			ItemStack dropped = event.getItemDrop().getItemStack();
			String droppedName = StringUtils.stripColor(dropped.getItemMeta().getDisplayName());
			for (BearFairKit kit : BearFairKit.values()) {
				if (kit.getItems().contains(dropped) && !droppedName.equalsIgnoreCase("Basketball")) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		if (event.getFrom().equals(BearFair20.getWorld()))
			removeKits(event.getPlayer());
	}

}
