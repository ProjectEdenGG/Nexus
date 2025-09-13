package gg.projecteden.nexus.features.minigames.listeners;

import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MatchDamageListener implements Listener {

	@Data
	private static final class MinigamerDamageEventData {
		private Minigamer victim, attacker;
		private Entity damager;
		private Projectile projectile;

		public MinigamerDamageEventData(Minigamer victim, EntityDamageEvent event) {
			this.victim = victim;
			if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
				damager = damageByEntityEvent.getDamager();
				if (damager instanceof Player) {
					attacker = Minigamer.of(damager);
				} else if (damager instanceof Projectile) {
					projectile = (Projectile) damager;
					if (projectile.getShooter() instanceof Player)
						attacker = Minigamer.of((Player) projectile.getShooter());
				}
			}
		}
	}

	// TODO: Prevent damage of hanging entities/armor stands/etc
	@EventHandler(ignoreCancelled = true)
	public void on(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
			if (!(entityDamageByEntityEvent.getDamager() instanceof FallingBlock)) {
				on(entityDamageByEntityEvent);
				return;
			}
		}

		if (!(event.getEntity() instanceof Player player))
			return;

		Minigamer victim = Minigamer.of(player);

		if (victim.isSpectating()) {
			event.setCancelled(true);
			return;
		}

		if (!victim.isPlaying())
			return;

		// block damage while in lobby
		if (!victim.isAlive() || victim.isRespawning() || !victim.getMatch().isStarted()) {
			event.setCancelled(true);
			return;
		}

		Mechanic mechanic = victim.getMatch().getMechanic();

		// Handled by death event
		if (!(event.getFinalDamage() < victim.getPlayer().getHealth()))
			return;

		MinigamerDamageEvent damageEvent = new MinigamerDamageEvent(victim, event);
		if (!damageEvent.callEvent()) {
			event.setCancelled(true);
			return;
		}

		mechanic.onDamage(damageEvent);

		if (damageEvent.isCancelled())
			event.setCancelled(true);
	}

	// Purposefully not an @EventHandler, see on(EntityDamageEvent)
	public void on(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		final var data = new MinigamerDamageEventData(Minigamer.of(player), event);

		Minigamer victim = data.getVictim();

		if ((victim.isPlaying() && !victim.getMatch().isStarted()) || !victim.isAlive() || victim.isRespawning()) {
			event.setCancelled(true);
			return;
		}

		Minigamer attacker = data.getAttacker();
		Projectile projectile = data.getProjectile();

		if (victim.getMatch() == null || victim.getTeam() == null) {
			if (attacker != null && (attacker.getMatch() == null || attacker.getTeam() == null)) {
				if (victim.getMatch() != null && attacker.getMatch() == null) {
					// Normal player damaging someone in a minigame
					event.setCancelled(true);
				}
				if (victim.getMatch() == null && attacker.getMatch() != null) {
					// Minigamer damaging normal player
					event.setCancelled(true);
				}
			}
			// Neither in minigames, ignore
			return;
		}

		if (attacker != null) {
			if (!(victim.isPlaying() && attacker.isPlaying()))
				return;

			if ((victim.isRespawning() || attacker.isRespawning()) || victim.equals(attacker)) {
				event.setCancelled(true);
				return;
			}

			if (!victim.getMatch().isStarted()) {
				event.setCancelled(true);
				return;
			}
		}

		if (attacker == null || victim.getMatch().equals(attacker.getMatch())) {
			// Same match
			Mechanic mechanic = victim.getMatch().getMechanic();
			if (attacker != null && victim.getTeam().equals(attacker.getTeam()) && mechanic instanceof TeamMechanic teamMechanic && !teamMechanic.allowFriendlyFire()) {
				// Friendly fire
				event.setCancelled(true);
			} else {
				// Damaged by opponent
				double newHealth = victim.getPlayer().getHealth() - event.getFinalDamage();

				if (attacker != null && event.getDamager() instanceof Arrow)
					attacker.getPlayer().playSound(attacker.getPlayer().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);

				if (newHealth > 0) {
					MinigamerDamageEvent damageEvent = new MinigamerDamageEvent(victim, attacker, event);
					if (!damageEvent.callEvent()) {
						event.setCancelled(true);
						return;
					}

					if (attacker != null && event.getDamager() instanceof Arrow)
						attacker.tell("&7" + victim.getNickname() + " is on &c" + Nameplates.HP_FORMAT.format(newHealth) + " &7HP");

					mechanic.onDamage(damageEvent);

					if (damageEvent.isCancelled())
						event.setCancelled(true);
					return;
				}

				if (projectile != null)
					projectile.remove();
			}
		} else {
			// Different matches
			event.setCancelled(true);
		}
	}

	// specialized message for updating nameplates in minigames when they take damage
	// main damage method is above
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public final void onDamageUpdateNameplate(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player && Minigamer.of(player).isPlaying())
			Tasks.wait(1, () -> Nameplates.get().getNameplateManager().update(player));
	}

	@EventHandler(ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Minigamer victim = Minigamer.of(event.getPlayer());

		if (!victim.isPlaying())
			return;

		Mechanic mechanic = victim.getMatch().getMechanic();

		event.setCancelled(true);

		final var data = new MinigamerDamageEventData(victim, event.getPlayer().getLastDamageCause());

		MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(victim, data.getAttacker(), event);
		if (mechanic.useNaturalDeathMessage())
			deathEvent.setDeathMessage(event.deathMessage());

		if (!deathEvent.callEvent())
			return;

		if (!victim.getMatch().isEnded())
			mechanic.onDeath(deathEvent);
	}

	@EventHandler
	public void onEnterKillRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying())
			return;

		if (!minigamer.getMatch().isStarted() || !minigamer.isAlive())
			return;

		Mechanic mechanic = minigamer.getMatch().getMechanic();

		Arena arena = minigamer.getMatch().getArena();
		if (arena.ownsRegion(event.getRegion(), "kill"))
			mechanic.kill(minigamer);
	}

}
