package me.pugabyte.bncore.features.oldminigames;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.oldminigames.murder.Murder;
import org.bukkit.plugin.Plugin;

public class OldMinigames {
	public static Murder murder;

	public OldMinigames() {
		Plugin minigames = BNCore.getInstance().getServer().getPluginManager().getPlugin("Minigames");

		if (minigames != null && minigames.isEnabled()) {
			murder = new Murder();
			BNCore.registerListener(new MinigameListener());
		}
	}

}
