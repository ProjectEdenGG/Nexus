package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.clearinventory.models.ClearInventoryPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ClearInventory {
	public final static String PREFIX = BNCore.getPrefix("ClearInventory");
	private Map<Player, ClearInventoryPlayer> players = new HashMap<>();

	public ClearInventory() {
		new ClearInventoryCommand();
		new ClearInventoryToggleCommand();
		new ClearInventoryListener();
	}

	public ClearInventoryPlayer getPlayer(Player player) {
		if (!players.containsKey(player)) {
			players.put(player, new ClearInventoryPlayer(player));
		}

		return players.get(player);
	}

}
