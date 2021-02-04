package me.pugabyte.nexus.features.minigames.models.mechanics;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Match.MatchTasks.MatchTaskType;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks.Countdown;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.nexus.utils.StringUtils.left;
import static me.pugabyte.nexus.utils.StringUtils.plural;

public abstract class Mechanic implements Listener {

	public Mechanic() {
		Nexus.registerListener(this);
	}

	public abstract String getName();

	public String getPrefix() {
		return StringUtils.getPrefix(this.getClass());
	}

	public abstract String getDescription();

	public abstract ItemStack getMenuItem();

	public boolean isTeamGame() {
		return false;
	}

	public GameMode getGameMode() {
		return GameMode.ADVENTURE;
	}

	public boolean canDropItem(ItemStack item) {
		return false;
	}

	public void onInitialize(MatchInitializeEvent event) {
		Match match = event.getMatch();
		int taskId = match.getTasks().repeat(1, Time.SECOND, () -> {
			if (match.getScoreboard() != null)
				match.getScoreboard().update();

			if (match.getScoreboardTeams() != null)
				match.getScoreboardTeams().update();
		});

		match.getTasks().register(MatchTaskType.SCOREBOARD, taskId);
	}

	public void onStart(MatchStartEvent event) {
		Match match = event.getMatch();
		match.broadcast("Starting match");
		match.broadcastNoPrefix("");
		int lives = match.getArena().getLives();
		if (lives > 0)
			match.getMinigamers().forEach(minigamer -> minigamer.setLives(lives));
		else
			match.getMinigamers().forEach(minigamer -> {
				if (minigamer.getTeam().getLives() > 0)
					minigamer.setLives(minigamer.getTeam().getLives());
			});

		int beginDelay = match.getArena().getBeginDelay();
		if (beginDelay > 0) {
			int taskId = match.getTasks().countdown(Countdown.builder()
					.duration(Time.SECOND.x(beginDelay))
					.onSecond(i -> {
						if (Arrays.asList(60, 30, 15, 5, 4, 3, 2, 1).contains(i))
							match.broadcast("&7Starting in &e" + plural(i + " second", i) + "...");
					})
					.onComplete(() -> {
						MatchBeginEvent beginEvent = new MatchBeginEvent(match);
						if (beginEvent.callEvent())
							begin(beginEvent);
					}));

			match.getTasks().register(MatchTaskType.BEGIN_DELAY, taskId);
		} else {
			MatchBeginEvent beginEvent = new MatchBeginEvent(match);
			if (beginEvent.callEvent())
				begin(beginEvent);
		}
	}

	public void begin(Match match) {
		MatchBeginEvent beginEvent = new MatchBeginEvent(match);
		if (beginEvent.callEvent())
			begin(beginEvent);
	}

	public void begin(MatchBeginEvent event) {
		event.getMatch().setBegun(true);
	}

	public void end(Match match) {
		match.end();
	}

	public void onEnd(MatchEndEvent event) {
		if (event.getMatch().isStarted())
			announceWinners(event.getMatch());
	}

	public abstract void processJoin(Minigamer minigamer);

	public void onJoin(MatchJoinEvent event) {
		Minigamer minigamer = event.getMinigamer();
		minigamer.getMatch().broadcast("&e" + minigamer.getPlayer().getName() + " &3has joined");
		tellMapAndMechanic(minigamer);
	}

	public void onQuit(MatchQuitEvent event) {
		Minigamer minigamer = event.getMinigamer();
		minigamer.getMatch().broadcast("&e" + minigamer.getPlayer().getName() + " &3has quit");
		if (minigamer.getMatch().isStarted() && shouldBeOver(minigamer.getMatch()))
			minigamer.getMatch().end();
	}

	public void onDamage(MinigamerDamageEvent event) {}

	public void onDeath(MinigamerDeathEvent event) {
		// TODO: Autobalancing
		event.broadcastDeathMessage();
		if (event.getMatch().getScoreboard() != null)
			event.getMatch().getScoreboard().update();
		if (shouldBeOver(event.getMatch()))
			event.getMatch().end();
	}

	public void kill(Minigamer minigamer) {
		kill(minigamer, null);
	}

	public void kill(Minigamer victim, Minigamer attacker) {
		MinigamerDeathEvent event = new MinigamerDeathEvent(victim, attacker);
		if (!event.callEvent()) return;

		onDeath(event);
	}

	public boolean shouldClearInventory() {
		return true;
	}

	public boolean allowFly() {
		return false;
	}

	public void tellMapAndMechanic(Minigamer minigamer) {
		Arena arena = minigamer.getMatch().getArena();
		String mechanicName = arena.getMechanic().getName();
		String arenaName = arena.getDisplayName();
		minigamer.tell("You are playing &e" + mechanicName + (mechanicName.equals(arenaName) ? "" : " &3on &e" + arenaName));
	}

	public abstract void announceWinners(Match match);

	public int getWinningScore(Collection<Integer> scores) {
		if (scores.size() == 0)
			return 0;
		return Collections.max(scores);
	}

	public void balance(Minigamer minigamer) {
		balance(Collections.singletonList(minigamer));
	}

	public abstract void balance(List<Minigamer> minigamers);

	public String getScoreboardTitle(Match match) {
		return left(match.getArena().getName(), 16);
	}

	public Map<String, Integer> getScoreboardLines(Match match) {
		Map<String, Integer> lines = new HashMap<>();

		if (match.getMechanic() instanceof TeamMechanic)
			for (Team team : match.getAliveTeams())
				lines.put("- " + team.getColoredName(), team.getScore(match));

		// TODO: Max number of lines is 15, only show max/min scores
		for (Minigamer minigamer : match.getMinigamers())
			if (minigamer.isAlive())
				lines.put(minigamer.getColoredName(), minigamer.getScore());
			else
				// &r to force last
				lines.put("&r&c&m" + minigamer.getName(), minigamer.getScore());

		return lines;
	}

	public Map<String, Integer> getScoreboardLines(Minigamer minigamer) {
		return new HashMap<>();
	}

	public Map<String, Integer> getScoreboardLines(Match match, Team team) {
		return new HashMap<>();
	}

	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		if (ActionGroup.CLICK_BLOCK.applies(event))
			if (event.getClickedBlock() != null)
				if (!canUseBlock(minigamer, event.getClickedBlock())) {
					Nexus.log("Cancelling interact");
					event.setCancelled(true);
					return;
				}
	}

	public boolean canUseBlock(Minigamer minigamer, Block block) {
		return minigamer.getMatch().getArena().canUseBlock(block.getType());
	}

	public abstract boolean shouldBeOver(Match match);

	public boolean shuffleSpawnpoints() {
		return true;
	}

	// Reflection utils

	public List<Class<? extends Mechanic>> getSuperclasses() {
		List<Class<? extends Mechanic>> superclasses = new ArrayList<>();
		Class<? extends Mechanic> clazz = this.getClass();
		while (clazz.getSuperclass() != Object.class) {
			superclasses.add(clazz);

			clazz = (Class<? extends Mechanic>) clazz.getSuperclass();
		}

		return superclasses;
	}

	public <T> T getAnnotation(Class<? extends Annotation> annotation) {
		for (Class<? extends Mechanic> mechanic : getSuperclasses()) {
			Annotation result = mechanic.getAnnotation(annotation);
			if (result != null) {
				return (T) result;
			}
		}

		return null;
	}

	public boolean canOpenInventoryBlocks() {
		return false;
	}

	public boolean isInRegion(Match match, Block block, String region) {
		return match.getArena().isInRegion(block, region);
	}

}
