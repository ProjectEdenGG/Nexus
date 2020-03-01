package me.pugabyte.bncore.features.oldminigames;

import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.minigame.Minigame;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MinigameListener implements Listener {

	@EventHandler
	public void onOldMinigameCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toLowerCase().matches("/mgm (quit|leave).*")) {
			event.setCancelled(true);
			Player player = event.getPlayer();
			Minigamer minigamer = PlayerManager.get(player);
			if (minigamer.isPlaying())
				minigamer.quit();
			else {
				WorldGuardUtils worldGuardUtils = new WorldGuardUtils(player.getWorld());
				if (worldGuardUtils.getRegionsLikeAt(player.getLocation(), "mobarena_.*").size() > 0)
					Utils.runCommand(player, "ma leave");
				else
					Utils.runCommand(player, "minigames:mgm quit");
			}
		}
	}

	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onMinigameStart(StartMinigameEvent event) {
		try {
			Minigame minigame = event.getMinigame();
			String name = minigame.getGametypeName().toLowerCase();
			if (name.equals("walls"))
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "clearentitieswalls");
			 else if (name.toLowerCase().matches("quake|ffa|one vs one|1v1|1 v 1|dogfighting|oitq"))
				Tasks.wait(2, () -> MinigameUtils.shufflePlayers(minigame));
		} catch (NullPointerException ignore) {}
	}
}
