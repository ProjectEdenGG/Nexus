package me.pugabyte.bncore.features.rainbowarmour;

import me.pugabyte.bncore.features.rainbowarmour.models.RainbowArmourPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RainbowArmour {
	public HashMap<Player, RainbowArmourPlayer> enabledPlayers = new HashMap<>();

	public RainbowArmour() {
		new RainbowArmourCommand();
		new RainbowArmourListener();
	}

	public RainbowArmourPlayer getPlayer(Player player) {
		if (enabledPlayers.containsKey(player)) {
			return enabledPlayers.get(player);
		}

		RainbowArmourPlayer rbaPlayer = new RainbowArmourPlayer(player, -1);
		enabledPlayers.put(player, rbaPlayer);
		return rbaPlayer;

	}


}
