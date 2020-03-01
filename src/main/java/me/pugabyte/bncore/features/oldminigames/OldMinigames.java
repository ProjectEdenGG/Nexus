package me.pugabyte.bncore.features.oldminigames;

import me.pugabyte.bncore.BNCore;
import org.bukkit.plugin.Plugin;

public class OldMinigames {

	public OldMinigames() {
		Plugin minigames = BNCore.getInstance().getServer().getPluginManager().getPlugin("Minigames");

		if (minigames != null && minigames.isEnabled()) {
			BNCore.registerListener(new MinigameListener());
		}
	}

}
