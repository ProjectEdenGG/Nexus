package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.features.clearinventory.models.ClearInventoryPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ClearInventory {
	public final static String PREFIX = Utils.getPrefix("ClearInventory");
	private Map<Player, ClearInventoryPlayer> players = new HashMap<>();

	public ClearInventory() {
		new ClearInventoryTabCompleter();
		new ClearInventoryListener();
	}

	public ClearInventoryPlayer getPlayer(Player player) {
		if (!players.containsKey(player)) {
			players.put(player, new ClearInventoryPlayer(player));
		}

		return players.get(player);
	}

}
