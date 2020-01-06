package me.pugabyte.bncore.features.minigames.models.mechanics;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Mechanic implements Listener {

	public Mechanic() {
		BNCore.registerListener(this);
	}

	public abstract String getName();

	public abstract String getDescription();

	public boolean isTeamGame() {
		return false;
	}

	public GameMode getGameMode() {
		return GameMode.ADVENTURE;
	}

	public void onInitialize(Match match) {}

	public void onStart(Match match) {}

	public void onEnd(Match match) {
		if (match.isStarted())
			announceWinners(match);
	}

	protected void onDamage(Minigamer victim, EntityDamageEvent event) {}

	public void onDeath(Minigamer victim) {
		// TODO: Autobalancing
		victim.getMatch().broadcast(victim.getColoredName() + " &3died");
	}

	public void onDeath(Minigamer victim, Minigamer killer) {
		// TODO: Autobalancing
		victim.getMatch().broadcast(victim.getColoredName() + " &3was killed by " + killer.getColoredName());
	}

	public void onJoin(Minigamer minigamer) {
		minigamer.getMatch().broadcast("&e" + minigamer.getPlayer().getName() + " &3has joined");
		Arena arena = minigamer.getMatch().getArena();
		minigamer.tell("You are playing &e" + arena.getMechanic().getName() + " &3on &e" + arena.getDisplayName());
	}

	public void onQuit(Minigamer minigamer) {
		minigamer.getMatch().broadcast("&e" + minigamer.getPlayer().getName() + " &3has quit");
		if (minigamer.getMatch().isStarted())
			checkIfShouldBeOver(minigamer.getMatch());
	}

	public void kill(Minigamer minigamer) {
		onDeath(minigamer);
	}

	public abstract void announceWinners(Match match);

	public int getWinningScore(Map<?, Integer> scores) {
		return Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getValue();
	}

	public abstract List<Minigamer> balance(List<Minigamer> minigamers);

	public boolean isArenaRegion(String regionName, Arena arena, String type) {
		return regionName.toLowerCase().matches(("^" + getName() + "_" + arena.getName() + "_" + type + "_[0-9]+$").toLowerCase());
	}

	public String getScoreboardTitle(Match match) {
		return match.getArena().getName();
	}

	public Map<String, Integer> getScoreboardLines(Match match) {
		Map<String, Integer> lines = new HashMap<>();

		if (match.getArena().getMechanic() instanceof TeamMechanic)
			for (Team team : match.getTeams())
				lines.put("- " + team.getColoredName(), team.getScore(match));

		// TODO: Max number of lines is 15, only show max/min scores
		for (Minigamer minigamer : match.getMinigamers())
			lines.put(minigamer.getColoredName(), minigamer.getScore());

		return lines;
	}

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

		if (!(victim.isPlaying(this) && attacker.isPlaying(this))) return;

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
				if (event.getDamage() >= victim.getPlayer().getHealth()) {
					event.setCancelled(true);
					MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(victim, attacker);
					Utils.callEvent(deathEvent);
					if (!deathEvent.isCancelled()) {
						mechanic.onDeath(victim, attacker);
						if (!victim.getMatch().isEnded()) {
							mechanic.kill(victim);
						}
					}
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
		if (victim.getMatch() == null || victim.getTeam() == null) return;
		if (!victim.isPlaying(this)) return;

		if (victim.isRespawning()) {
			event.setCancelled(true);
			return;
		}

		Mechanic mechanic = victim.getMatch().getArena().getMechanic();

		if (event.getDamage() < victim.getPlayer().getHealth()) {
			onDamage(victim, event);
			return;
		}

		event.setCancelled(true);

		MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(victim);
		Utils.callEvent(deathEvent);
		if (deathEvent.isCancelled()) return;

		if (victim.getMatch().isEnded()) return;

		kill(victim);
	}

	public abstract void checkIfShouldBeOver(Match match);

}
