package me.pugabyte.bncore.features.minigames.listeners;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.text.DecimalFormat;

public class MatchListener implements Listener {

	public MatchListener() {
		registerWaterDamageTask();
	}

	private void registerWaterDamageTask() {
		// TODO: Move to a task triggered by entering the region
		Tasks.repeat(20, 20, () ->
			Minigames.getActiveMinigamers().forEach(minigamer -> {
				if (minigamer.isInMatchRegion("waterdamage") && Utils.isInWater(minigamer.getPlayer()))
					minigamer.getPlayer().damage(1.25);
		}));
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (minigamer.getMatch() == null) return;
		if (minigamer.canTeleport()) return;

		event.setCancelled(true);
		minigamer.tell("You cannot teleport while in a game!");
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
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;

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
			if (victim.getMatch() != null && attacker.getMatch() == null) {
				// Normal player damaging someone in a minigame
				event.setCancelled(true);
			}
			if (victim.getMatch() == null && attacker.getMatch() != null) {
				// Minigamer damaging normal player
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

		if (!victim.getMatch().isStarted()) {
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
				double newHealth = victim.getPlayer().getHealth() - event.getFinalDamage();

				if (newHealth > 0) {
					MinigamerDamageEvent damageEvent = new MinigamerDamageEvent(victim, attacker, event);
					Utils.callEvent(damageEvent);
					if (damageEvent.isCancelled()) {
						event.setCancelled(true);
						return;
					}

					if (event.getDamager() instanceof Arrow)
						attacker.tell("&7" + victim.getName() + " is on &c" + new DecimalFormat("#.0").format(newHealth) + " &7HP");

					mechanic.onDamage(damageEvent);

					if (damageEvent.isCancelled())
						event.setCancelled(true);
					return;
				}

				event.setCancelled(true);

				if (event.getDamager() instanceof Arrow)
					attacker.getPlayer().playSound(attacker.getPlayer().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);

				MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(victim, attacker, event);
				Utils.callEvent(deathEvent);
				if (deathEvent.isCancelled()) return;

				if (!victim.getMatch().isEnded())
					mechanic.onDeath(deathEvent);
			}
		} else {
			// Different matches
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
			if (!(entityDamageByEntityEvent.getDamager() instanceof FallingBlock)) {
				onDamage(entityDamageByEntityEvent);
				return;
			}
		}

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

		if (victim.isRespawning() || !victim.getMatch().isStarted()) {
			event.setCancelled(true);
			return;
		}

		if (event.getFinalDamage() < victim.getPlayer().getHealth()) {
			MinigamerDamageEvent damageEvent = new MinigamerDamageEvent(victim, event);
			Utils.callEvent(damageEvent);
			if (damageEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			}

			mechanic.onDamage(damageEvent);

			if (damageEvent.isCancelled())
				event.setCancelled(true);
			return;
		}

		event.setCancelled(true);

		MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(victim, event);
		Utils.callEvent(deathEvent);
		if (deathEvent.isCancelled()) return;

		if (!victim.getMatch().isEnded())
			mechanic.onDeath(deathEvent);
	}

	@EventHandler
	public void onEnterKillRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying()) return;
		if (!minigamer.getMatch().isStarted() || !minigamer.isAlive()) return;
		Mechanic mechanic = minigamer.getMatch().getArena().getMechanic();

		Arena arena = minigamer.getMatch().getArena();
		if (arena.ownsRegion(event.getRegion().getId(), "kill"))
			mechanic.kill(minigamer);
	}

	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if (!event.getEntity().getWorld().equals(Minigames.getGameworld())) return;
		// TODO: Entity pickups?
		if (!(event.getEntity() instanceof Player)) return;

		Arena arena = ArenaManager.getFromLocation(event.getItem().getLocation());
		if (arena == null) return;
		Match match = MatchManager.get(arena);
		Player player = (Player) event.getEntity();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isIn(match))
			event.setCancelled(true);
	}
}
