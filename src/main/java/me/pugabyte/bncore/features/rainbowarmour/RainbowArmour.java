package me.pugabyte.bncore.features.rainbowarmour;

import lombok.Getter;
import me.pugabyte.bncore.features.rainbowarmour.models.RainbowArmourPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RainbowArmour {
	@Getter
	private static HashMap<Player, RainbowArmourPlayer> enabledPlayers = new HashMap<>();

	public RainbowArmour() {
		new RainbowArmourListener();
	}

	public static RainbowArmourPlayer getPlayer(Player player) {
		if (!enabledPlayers.containsKey(player))
			enabledPlayers.put(player, new RainbowArmourPlayer(player, -1));

		return enabledPlayers.get(player);
	}


}
