package me.pugabyte.bncore.features.minigames.listeners;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MatchListener implements Listener {

	public MatchListener() {
		BNCore.registerListener(this);

		registerWaterDamageTask();
	}

	private void registerWaterDamageTask() {
		// TODO: Move to a task triggered by entering the region
		Utils.repeat(20, 20, () ->
			Minigames.getActiveMinigamers().forEach(minigamer -> {
				if (minigamer.isInMatchRegion("waterdamage") && Utils.isInWater(minigamer.getPlayer()))
					minigamer.getPlayer().damage(1.25);
		}));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (minigamer.getMatch() == null) return;

		minigamer.quit();
	}

	@EventHandler
	public void onMatchQuit(MatchQuitEvent event) {
		MatchManager.janitor();
	}



	// TODO: Break and place events

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying()) return;

		minigamer.getMatch().getArena().getMechanic().onPlayerInteract(minigamer, event);
	}

	// TODO: Prevent damage of hanging entities/armor stands/etc
	@EventHandler
	public void onDeath(EntityDamageByEntityEvent event) {
		Minigamer victim, attacker;

		if (event.getEntity() instanceof Player) {
			victim = PlayerManager.get((Player) event.getEntity());
		} else {
			return;
		}

		if (event.getDamager() instanceof Player) {
			attacker = PlayerManager.get((Player) event.getDamager());
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			if (projectile.getShooter() instanceof Player) {
				attacker = PlayerManager.get((Player) projectile.getShooter());
			} else {
				return;
			}
		} else {
			return;
		}

		if (victim.getMatch() == null || attacker.getMatch() == null
				|| victim.getTeam() == null || attacker.getTeam() == null) {
			if (victim.getMatch() == null && attacker.getMatch() != null) {
				// Normal player damaging someone in a minigame
				event.setCancelled(true);
			}
			// Neither in minigames, ignore
			return;
		}

		if (!(victim.isPlaying() && attacker.isPlaying())) return;

		if ((victim.isRespawning() || attacker.isRespawning()) || victim.equals(attacker)) {
			event.setCancelled(true);
			return;
		}

		if (victim.getMatch().equals(attacker.getMatch())) {
			// Same match
			Mechanic mechanic = victim.getMatch().getArena().getMechanic();
			if (victim.getTeam().equals(attacker.getTeam()) && mechanic.isTeamGame()) {
				// Friendly fire
				event.setCancelled(true);
			} else {
				// Damaged by opponent
				if (event.getDamage() < victim.getPlayer().getHealth()) {
					mechanic.onDamage(victim, event);
					return;
				}

				event.setCancelled(true);
				if (!victim.getMatch().isEnded()) {
					mechanic.kill(victim, attacker);
				}
			}
		} else {
			// Different matches
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDeath(EntityDamageEvent event) {
		Minigamer victim;

		if (event.getEntity() instanceof Player) {
			victim = PlayerManager.get((Player) event.getEntity());
		} else {
			return;
		}


		// Ignore damage by entity (see above)
		if (event.getCause().name().contains("ENTITY")) return;
		if (!victim.isPlaying()) return;
		Mechanic mechanic = victim.getMatch().getArena().getMechanic();

		if (victim.isRespawning()) {
			event.setCancelled(true);
			return;
		}

		if (event.getDamage() < victim.getPlayer().getHealth()) {
			mechanic.onDamage(victim, event);
			return;
		}

		event.setCancelled(true);
		if (!victim.getMatch().isEnded())
			mechanic.kill(victim);
	}

	@EventHandler
	public void onEnterKillRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying()) return;
		Mechanic mechanic = minigamer.getMatch().getArena().getMechanic();

		Arena arena = minigamer.getMatch().getArena();
		if (arena.ownsRegion(event.getRegion().getId(), "kill"))
			mechanic.kill(minigamer);
	}
}
