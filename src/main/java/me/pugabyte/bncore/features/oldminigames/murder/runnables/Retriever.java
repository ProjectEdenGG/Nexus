package me.pugabyte.bncore.features.oldminigames.murder.runnables;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Retriever extends BukkitRunnable {

	private Player player;
	private int time;

	public Retriever(Player player) {
		this.player = player;
		String[] name = player.getInventory().getItem(1).getItemMeta().getDisplayName().split(" in ");
		this.time = Integer.parseInt(ChatColor.stripColor(name[1]));
	}

	@Override
	public void run() {
		ItemStack item = player.getInventory().getItem(1);

		// If its not the retriever item, they found the knife, so cancel
		if (item == null || item.getType() != Material.EYE_OF_ENDER)
			this.cancel();

		ItemMeta meta = item.getItemMeta();

		if (time > 0) {
			meta.setDisplayName(ChatColor.YELLOW + "Retrieve the knife in " + ChatColor.RED + time--);
			player.getInventory().getItem(1).setItemMeta(meta);
		} else {
			meta.setDisplayName(ChatColor.YELLOW + "Retrieve the knife");
			player.getInventory().getItem(1).setItemMeta(meta);
			this.cancel();
		}
	}
}