package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.features.clearinventory.models.ClearInventoryPlayer;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ClearInventory {
	public final static String PREFIX = StringUtils.getPrefix("ClearInventory");
	private static Map<Player, ClearInventoryPlayer> players = new HashMap<>();

	public ClearInventory() {
		new ClearInventoryListener();
	}

	public static ClearInventoryPlayer getPlayer(Player player) {
		if (!players.containsKey(player))
			players.put(player, new ClearInventoryPlayer(player));

		return players.get(player);
	}

}
