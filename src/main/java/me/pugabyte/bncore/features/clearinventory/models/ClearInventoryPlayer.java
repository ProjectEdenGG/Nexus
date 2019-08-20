package me.pugabyte.bncore.features.clearinventory.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.bncore.features.clearinventory.ClearInventory.PREFIX;

public class ClearInventoryPlayer {
	private Player player;
	private Map<String, ItemStack[]> cache = new HashMap<>();

	public ClearInventoryPlayer(Player player) {
		this.player = player;
	}

	String getKey() {
		return player.getWorld().getName().toLowerCase() + "-" + player.getGameMode().name().toLowerCase();
	}

	public void addCache() {
		cache.put(getKey(), player.getInventory().getContents());
	}

	public void removeCache() {
		cache.remove(getKey());
	}

	public void restoreCache() {
		if (cache.containsKey(getKey())) {
			for (ItemStack itemStack : player.getInventory().getContents()) {
				if (itemStack != null) {
					player.sendMessage(PREFIX + "Your inventory must be empty to restore an undo");
					return;
				}
			}
			player.getInventory().setContents(cache.get(getKey()));
			removeCache();
			player.sendMessage(PREFIX + "Inventory restored");
		} else {
			player.sendMessage(PREFIX + "There's nothing to undo!");
		}
	}

}
