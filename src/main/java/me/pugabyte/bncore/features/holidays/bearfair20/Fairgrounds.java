package me.pugabyte.bncore.features.holidays.bearfair20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds.Archery;
import me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds.Basketball;
import me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds.Frogger;
import me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds.PugDunk;
import me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds.Reflection;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Fairgrounds implements Listener {

	public Fairgrounds() {
		BNCore.registerListener(this);
		new Timer("      PugDunk", PugDunk::new);
		new Timer("      Archery", Archery::new);
		new Timer("      Frogger", Frogger::new);
		new Timer("      Basketball", Basketball::new);
		new Timer("      Reflection", Reflection::new);
	}

	public static void giveKit(BearFairKit kit, Player player) {
		if (slotsTaken(player) <= (36 - kit.items.size())) {
			Utils.giveItems(player, kit.items);
		}
	}

	private static int slotsTaken(Player player) {
		ItemStack[] items = player.getInventory().getContents();
		int count = 0;
		for (ItemStack item : items) {
			if (item == null || Utils.isNullOrAir(item.getType())) continue;
			count++;
		}
		return count;
	}

	public static void removeKits(Player player) {
		ItemStack[] items = player.getInventory().getContents();
		for (ItemStack item : items) {
			if (item == null) continue;
			if (!item.hasItemMeta()) continue;
			ItemMeta meta = item.getItemMeta();
			if (meta == null) continue;
			if (!meta.hasLore()) continue;
			List<String> lore = meta.getLore();
			if (lore == null) continue;

			for (String str : lore) {
				if (StringUtils.stripColor(str).contains("BearFair20")) {
					player.getInventory().remove(item);
					break;
				}
			}
		}
	}

	public enum BearFairKit {
		BOW_AND_ARROW(
				new ItemBuilder(Material.BOW)
						.enchant(Enchantment.ARROW_INFINITE)
						.lore("&eBearFair20 Bow")
						.build(),
				new ItemBuilder(Material.ARROW)
						.lore("&eBearFair20 Arrow")
						.build()
		),
		MINECART(
				new ItemBuilder(Material.MINECART)
						.lore("&eBearFair20 Minecart")
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
		ItemStack basketballConfig = (ItemStack) BNCore.getInstance().getConfig().get("minigames.lobby.basketball.item");
		if (basketballConfig == null)
			basketballConfig = new ItemStack(Material.SKELETON_SKULL);

		ItemStack basketball = basketballConfig.clone();

		ItemMeta meta = basketball.getItemMeta();
		meta.setLore(Collections.singletonList(colorize("&eBearFair20 Basketball")));
		meta.setDisplayName(colorize("&6&lBasketball"));
		basketball.setItemMeta(meta);

		return basketball;
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String id = event.getRegion().getId();
		if (id.contains(BearFair20.mainRg + "_bow_"))
			giveKit(BearFairKit.BOW_AND_ARROW, event.getPlayer());
		if (id.contains(BearFair20.mainRg + "_minecart_"))
			giveKit(BearFairKit.MINECART, event.getPlayer());
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		String id = event.getRegion().getId();
		String bowRg = BearFair20.mainRg + "_bow_";
		String minecartRg = BearFair20.mainRg + "_minecart_";
		if (id.contains(bowRg) || id.contains(minecartRg)) {
			removeKits(event.getPlayer());
		}
	}

	public static void startMerryGoRound() {
		Location loc = new Location(BearFair20.world, -936, 136, -1588);
		loc.getBlock().setType(Material.REDSTONE_BLOCK);
		Tasks.wait(Time.SECOND.x(20), () -> loc.getBlock().setType(Material.AIR));
	}


}
