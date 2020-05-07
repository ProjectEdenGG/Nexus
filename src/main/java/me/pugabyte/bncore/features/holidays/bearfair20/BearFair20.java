package me.pugabyte.bncore.features.holidays.bearfair20;

import lombok.Data;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Halloween;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

@Data
public class BearFair20 implements Listener {

	public static World world = Bukkit.getWorld("safepvp");
	public static String mainRg = "bearfair2020";

	public BearFair20() {
		BNCore.registerListener(this);
		new Fairgrounds();
		new Halloween();
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
				if (StringUtils.stripColor(str).contains("BearFair20 Item")) {
					player.getInventory().remove(item);
					break;
				}
			}
		}
	}

	public enum BearFairKit {
		BOW_AND_ARROW(
				new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_INFINITE).lore("&eBearFair20 Item").build(),
				new ItemBuilder(Material.ARROW).lore("&eBearFair20 Item").build()
		),
		MINECART(
				new ItemBuilder(Material.MINECART).lore("&eBearFair20 Item").build()
		);

		List<ItemStack> items;

		BearFairKit(ItemStack... items) {
			this.items = Arrays.asList(items);
		}
	}


}
