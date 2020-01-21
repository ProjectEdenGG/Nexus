package me.pugabyte.bncore.features.minigames.models.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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

	public abstract ItemStack getMenuItem();

	public boolean isTeamGame() {
		return false;
	}

	public GameMode getGameMode() {
		return GameMode.ADVENTURE;
	}

	public void onInitialize(MatchInitializeEvent event) {

	}

	public void onStart(MatchStartEvent event) {
		Match match = event.getMatch();
		match.broadcast("Starting match");
		match.getTasks().repeat(20, 20, () -> match.getScoreboard().update());
		int lives = match.getArena().getLives();
		if (lives > 0)
			match.getMinigamers().forEach(minigamer -> minigamer.setLives(lives));
	}

	public void onEnd(MatchEndEvent event) {
		if (event.getMatch().isStarted())
			announceWinners(event.getMatch());
	}

	public void onJoin(MatchJoinEvent event) {
		Minigamer minigamer = event.getMinigamer();
		minigamer.getMatch().broadcast("&e" + minigamer.getPlayer().getName() + " &3has joined");
		Arena arena = minigamer.getMatch().getArena();
		minigamer.tell("You are playing &e" + arena.getMechanic().getName() + " &3on &e" + arena.getDisplayName());
	}

	public void onQuit(MatchQuitEvent event) {
		Minigamer minigamer = event.getMinigamer();
		minigamer.getMatch().broadcast("&e" + minigamer.getPlayer().getName() + " &3has quit");
		if (minigamer.getMatch().isStarted() && shouldBeOver(minigamer.getMatch()))
			minigamer.getMatch().end();
	}

	protected void onDamage(Minigamer victim, EntityDamageEvent event) {}

	public void onDeath(MinigamerDeathEvent event) {
		// TODO: Autobalancing
	}

	public void kill(Minigamer minigamer) {
		kill(minigamer, null);
	}

	public void kill(Minigamer victim, Minigamer attacker) {
		MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(victim, attacker);
		Utils.callEvent(deathEvent);
		if (deathEvent.isCancelled()) return;

		onDeath(deathEvent);
		deathEvent.broadcastDeathMessage();
		victim.getMatch().getScoreboard().update();
		if (shouldBeOver(victim.getMatch()))
			victim.getMatch().end();
	}

	public boolean shouldClearInventory() {
		return true;
	}

	public abstract void announceWinners(Match match);

	public int getWinningScore(Map<?, Integer> scores) {
		return Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getValue();
	}

	public abstract List<Minigamer> balance(List<Minigamer> minigamers);

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
			if (minigamer.isAlive())
				lines.put(minigamer.getColoredName(), minigamer.getScore());
			else
				lines.put("&c&m" + minigamer.getName(), minigamer.getScore());

		return lines;
	}

	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)
			if (event.getClickedBlock() != null)
				if (!minigamer.getMatch().getArena().canUseBlock(event.getClickedBlock().getType())) {
					BNCore.log("Cancelling interact");
					event.setCancelled(true);
					return;
				}
	}

	// TODO: Break and place events

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isIn(this)) return;

		onPlayerInteract(minigamer, event);
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
				if (event.getDamage() < victim.getPlayer().getHealth()) {
					onDamage(victim, event);
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
		if (victim.getMatch() == null || victim.getTeam() == null) return;
		if (!victim.isPlaying(this)) return;

		if (victim.isRespawning()) {
			event.setCancelled(true);
			return;
		}

		if (event.getDamage() < victim.getPlayer().getHealth()) {
			onDamage(victim, event);
			return;
		}

		event.setCancelled(true);
		if (!victim.getMatch().isEnded())
			kill(victim);
	}

	@EventHandler
	public void onEnterKillRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!(minigamer.isPlaying(this))) return;

		Arena arena = minigamer.getMatch().getArena();
		if (arena.ownsRegion(event.getRegion().getId(), "kill"))
			kill(minigamer);
	}

	public abstract boolean shouldBeOver(Match match);

}
