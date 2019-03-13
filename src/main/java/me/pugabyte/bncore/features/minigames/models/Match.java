package me.pugabyte.bncore.features.minigames.models;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchBroadcastEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchLeaveEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchTimerTickEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.BNCore.colorize;

@Data
public class Match {
	@NonNull
	private Arena arena;
	private List<Minigamer> minigamers = new ArrayList<>();
	private boolean started = false;
	private boolean over = false;
	private Map<Team, Integer> scores = new HashMap<>();
	private MatchTimer timer;
	private MatchData matchData;

	public Optional<Minigamer> getPlayer(Player player) {
		return minigamers.stream()
				.filter(minigamer -> minigamer.getPlayer().getUniqueId().equals(player.getUniqueId()))
				.findFirst();
	}

	public void join(Minigamer minigamer) {
		MatchJoinEvent event = new MatchJoinEvent(this, minigamer);
		BNCore.callEvent(event);
		if (!event.isCancelled()) {

			if (started) {
				if (arena.canJoinLate()) {
					minigamers.add(minigamer);
					balance();
					teleportIn(minigamer);
				} else {
					minigamer.tell("This match has already started");
					return;
				}
			} else {
				minigamers.add(minigamer);
				arena.getLobby().join(minigamer);
			}

			arena.getMechanic().onJoin(minigamer);
		}
	}

	void quit(Minigamer minigamer) {
		if (minigamers.contains(minigamer)) {
			MatchLeaveEvent event = new MatchLeaveEvent(this, minigamer);
			BNCore.callEvent(event);
			if (!event.isCancelled()) {
				minigamers.remove(minigamer);
				minigamer.toLobby();
				minigamer.clearState();
				arena.getMechanic().onQuit(minigamer);
			}
		}
	}

	void start() {
		MatchStartEvent event = new MatchStartEvent(this);
		BNCore.callEvent(event);
		if (!event.isCancelled()) {
			started = true;
			clearEntities();
			balance();
			initalizeScores();
			teleportIn();
			startTimer();
			arena.getMechanic().onStart(this);
		}
	}

	public void end() {
		MatchEndEvent event = new MatchEndEvent(this);
		BNCore.callEvent(event);
		if (!event.isCancelled()) {
			over = true;
			broadcast("Match has ended");
			timer.stop();
			clearEntities();
			toLobby();
			clearStates();
			arena.getMechanic().onEnd(this);
			MatchManager.remove(this);
		}
	}

	private void startTimer() {
		if (arena.getSeconds() > 0)
			timer = new MatchTimer(this, arena.getSeconds());
	}

	private void clearEntities() {
		// TODO: Clearing dropped entities & arrows
	}

	private void balance() {
		minigamers = arena.getMechanic().balance(minigamers);
		minigamers.forEach(minigamer -> minigamer.tell("You are on team " + minigamer.getTeam().getColor() + minigamer.getTeam().getName()));
	}

	private void initalizeScores() {
		arena.getTeams().forEach(team -> scores.put(team, 0));
	}

	private void teleportIn() {
		arena.getTeams().forEach(team -> team.spawn(minigamers));
	}

	private void teleportIn(Minigamer minigamer) {
		minigamer.getTeam().spawn(Collections.singletonList(minigamer));
	}

	private void clearStates() {
		// TODO: Possibly edit ConditionalPerms to disallow voxel?
		// TODO: Unvanish
		minigamers.forEach(Minigamer::clearState);
	}

	private void toLobby() {
		minigamers.forEach(Minigamer::toLobby);
	}

	public void scored(Team team) {
		scored(team, 1);
	}

	public void scored(Team team, int score) {
		scores.put(team, scores.get(team) + score);
		if (scores.get(team) >= arena.getWinningScore()) {
			end();
		}
	}

	public void broadcast(String message) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message);
		BNCore.callEvent(event);
		if (!event.isCancelled()) {
			minigamers.forEach(minigamer -> minigamer.tell(colorize(event.getMessage())));
		}
	}

	public void broadcast(String message, Team team) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message, team);
		BNCore.callEvent(event);
		if (!event.isCancelled()) {
			minigamers.stream()
					.filter(minigamer -> minigamer.getTeam().equals(event.getTeam()))
					.collect(Collectors.toSet())
					.forEach(minigamer -> minigamer.tell(colorize(event.getMessage())));
		}
	}

	private class MatchTimer {
		private Match match;
		private int time;
		private List<Integer> broadcasts = Arrays.asList((60 * 10), (60 * 5), 60, 30, 15, 5, 4, 3, 2, 1);
		private BNCore bnCore = BNCore.getInstance();
		private int taskId;

		MatchTimer(Match match, int time) {
			this.match = match;
			this.time = time;
			start();
		}

		private void start() {
			taskId = bnCore.getServer().getScheduler().scheduleSyncRepeatingTask(bnCore, () -> {
				if (--time > 0) {
					MatchTimerTickEvent event = new MatchTimerTickEvent(match, time);
					BNCore.callEvent(event);
					if (broadcasts.contains(time)) {
						match.broadcast("&e" + time + " &7seconds left...");
					}
				} else {
					match.end();
					stop();
				}
			}, 0, 20);
		}

		private void stop() {
			bnCore.getServer().getScheduler().cancelTask(taskId);
		}

	}

}
