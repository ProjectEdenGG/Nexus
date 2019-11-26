package me.pugabyte.bncore.features.restoreinventory;

import me.pugabyte.bncore.features.restoreinventory.models.RestoreInventoryPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RestoreInventory {
	public static HashMap<Player, RestoreInventoryPlayer> restorers = new HashMap<>();

	public RestoreInventory() {
		new RestoreInventoryCommand();
	}

	public static void add(Player restorer, RestoreInventoryPlayer restoreInventoryPlayer) {
		restorers.put(restorer, restoreInventoryPlayer);
	}

	public static RestoreInventoryPlayer get(Player restorer) {
		return restorers.get(restorer);
	}
}