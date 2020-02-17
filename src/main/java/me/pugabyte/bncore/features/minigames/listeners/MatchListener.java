package me.pugabyte.bncore.features.minigames.listeners;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.DecimalFormat;

public class MatchListener implements Listener {

	public MatchListener() {
		BNCore.registerListener(this);

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
				double newDamage = victim.getPlayer().getHealth() - event.getFinalDamage();

				// Damaged by opponent
				if (newDamage > 0) {
					if (event.getDamager() instanceof Arrow)
						attacker.tell("&7" + victim.getName() + " is on &c" + new DecimalFormat("#.0").format(newDamage) + " &7HP");

					mechanic.onDamage(victim, event);
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
			onDamage((EntityDamageByEntityEvent) event);
			return;
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
			mechanic.onDamage(victim, event);
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
		if (arena.ownsRegion(event.getRegion().getId(), "kill")) {
			MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(minigamer, event);
			Utils.callEvent(deathEvent);
			if (deathEvent.isCancelled()) return;

			mechanic.onDeath(deathEvent);
		}
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

	@EventHandler
	public void onMatchInitialize_Regeneration(MatchInitializeEvent event) {
		for (Class<? extends Mechanic> mechanic : event.getMatch().getArena().getMechanic().getSuperclasses()) {
			Regenerating annotation = mechanic.getAnnotation(Regenerating.class);
			if (annotation != null)
				for (String type : annotation.value())
					regenerate(event.getMatch(), type);
		}
	}

	@EventHandler
	public void onMatchEnd_Regeneration(MatchEndEvent event) {
		for (Class<? extends Mechanic> mechanic : event.getMatch().getArena().getMechanic().getSuperclasses()) {
			Regenerating annotation = mechanic.getAnnotation(Regenerating.class);
			if (annotation != null)
				for (String type : annotation.value())
					regenerate(event.getMatch(), type);
		}
	}

	private void regenerate(Match match, String type) {
		String name = match.getArena().getMechanic().getName().toLowerCase();
		Minigames.getWorldGuardUtils().getRegionsLike(name + "_" + match.getArena().getName() + "_" + type + "_[0-9]+")
				.forEach(region -> {
					String file = (name + "/" + region.getId().replaceFirst(name + "_", "")).toLowerCase();
					Minigames.getWorldEditUtils().paste(file, region.getMinimumPoint());
				});
	}
}
