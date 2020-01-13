package me.pugabyte.bncore.features.oldminigames;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.minigame.Minigame;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.oldminigames.quake.Railgun;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MinigameListener implements Listener {
	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onMinigameStart(StartMinigameEvent event) {
		try {
			Minigame minigame = event.getMinigame();
			String name = minigame.getGametypeName().toLowerCase();
			if (name.equals("walls")) {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "clearentitieswalls");
			} else if (name.toLowerCase().matches("quake|ffa|one vs one|1v1|1 v 1|dogfighting|oitq")) {
				BNCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BNCore.getInstance(), () ->
								MinigameUtils.shufflePlayers(minigame),
						2L);
			}
		} catch (NullPointerException ex) {
			return;
		}
	}

	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		try {
			final Player player = event.getPlayer();
			if (player.getInventory().getItemInMainHand() == null) {
				return;
			}

			MinigamePlayer minigamePlayer = Minigames.plugin.getPlayerData().getMinigamePlayer(player);
			if (minigamePlayer == null) {
				return;
			}

			String gametype = minigamePlayer.getMinigame().getGametypeName();

			if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
				if (gametype.equalsIgnoreCase("parkour")) {
					if (player.getInventory().getItemInMainHand().getType() == Material.POISONOUS_POTATO) {
						Bukkit.dispatchCommand(player, "checkpoint tp");
					}
				} else if (gametype.equalsIgnoreCase("quake") || gametype.equalsIgnoreCase("dogfighting") || gametype.equalsIgnoreCase("murder")) {
					if (player.getInventory().getItemInMainHand().getType() == Material.IRON_HOE) {
						if (!minigamePlayer.isInMinigame()) {
							return;
						}

						Railgun railgun = (Railgun) new Railgun().clone();
						railgun.setPlayer(minigamePlayer);
						railgun.setHitbox(1D);
						railgun.setRange(200);
						railgun.setDamage(1000);
						railgun.setShouldDamageWithConsole(gametype.equalsIgnoreCase("murder"));
						if (railgun.canShoot()) {
							railgun.shoot();
							if (!gametype.equalsIgnoreCase("dogfighting")) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skexpguncooldown " + minigamePlayer.getPlayer().getName());
							}
						}
					}
				}
			}
		} catch (NullPointerException ex) {
		}
	}
}
