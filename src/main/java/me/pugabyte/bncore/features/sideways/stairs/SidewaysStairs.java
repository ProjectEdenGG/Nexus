package me.pugabyte.bncore.features.sideways.stairs;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.sideways.stairs.models.SidewaysStairsPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SidewaysStairs {
	public static final String PREFIX = BNCore.getPrefix("SidewaysStairs");
	static Map<Player, SidewaysStairsPlayer> playerData = new HashMap<>();

	public SidewaysStairs() {
		new SidewaysStairsCommand();
		new SidewaysStairsListener();
	}

}
