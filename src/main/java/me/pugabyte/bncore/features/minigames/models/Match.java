package me.pugabyte.bncore.features.minigames.models;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchBroadcastEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.MultiplayerMechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.Utils.colorize;

@Data
public class Match {
	@NonNull
	private Arena arena;
	@ToString.Exclude
	private List<Minigamer> minigamers = new ArrayList<>();
	private boolean initialized = false;
	private boolean started = false;
	private boolean ended = false;
	private Map<Team, Integer> scores = new HashMap<>();
	private MatchTimer timer;
	private MatchData matchData;

	public Optional<Minigamer> getPlayer(Player player) {
		return minigamers.stream()
				.filter(minigamer -> minigamer.getPlayer().getUniqueId().equals(player.getUniqueId()))
				.findFirst();
	}

	public boolean join(Minigamer minigamer) {
		MatchJoinEvent event = new MatchJoinEvent(this, minigamer);
		Utils.callEvent(event);
		if (event.isCancelled()) return false;

		if (!initialized) {
			arena.getMechanic().onInitialize(this);
			initialized = true;
		}

		if (started) {
			if (arena.canJoinLate()) {
				minigamers.add(minigamer);
				balance();
				teleportIn(minigamer);
			} else {
				minigamer.tell("This match has already started");
				return false;
			}
		} else {
			minigamers.add(minigamer);
			arena.getLobby().join(minigamer);
		}

		arena.getMechanic().onJoin(minigamer);
		return true;
	}

	void quit(Minigamer minigamer) {
		if (!minigamers.contains(minigamer)) return;

		MatchQuitEvent event = new MatchQuitEvent(this, minigamer);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		minigamer.toLobby();
		minigamer.clearState();
		arena.getMechanic().onQuit(minigamer);
		minigamers.remove(minigamer);
	}

	public void start() {
		MatchStartEvent event = new MatchStartEvent(this);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		started = true;
		clearEntities();
		balance();
		initializeScores();
		teleportIn();
		startTimer();
		arena.getMechanic().onStart(this);
	}

	public void end() {
		MatchEndEvent event = new MatchEndEvent(this);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		ended = true;
		broadcast("Match has ended");
		stopTimer();
		clearEntities();
		clearStates();
		toLobby();
		arena.getMechanic().onEnd(this);
		MatchManager.remove(this);
	}

	private void startTimer() {
		if (arena.getSeconds() > 0)
			timer = new MatchTimer(this, arena.getSeconds());
	}

	private void stopTimer() {
		if (timer != null)
			timer.stop();
	}

	private void clearEntities() {
		// TODO: Clearing dropped entities & arrows
	}

	private void balance() {
		minigamers = arena.getMechanic().balance(minigamers);
		if (arena.getMechanic() instanceof TeamMechanic)
			minigamers.forEach(minigamer -> minigamer.tell("You are on team " + minigamer.getTeam().getColoredName()));
	}

	private void initializeScores() {
		arena.getTeams().forEach(team -> scores.put(team, 0));
	}

	private void teleportIn() {
		arena.getTeams().forEach(team -> team.spawn(minigamers));
	}

	private void teleportIn(Minigamer minigamer) {
		minigamer.getTeam().spawn(Collections.singletonList(minigamer));
	}

	private void clearStates() {
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
		Utils.callEvent(event);
		if (!event.isCancelled()) {
			minigamers.forEach(minigamer -> minigamer.tell(colorize(event.getMessage())));
		}
	}

	public void broadcast(String message, Team team) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message, team);
		Utils.callEvent(event);
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
		private int taskId;

		MatchTimer(Match match, int time) {
			this.match = match;
			this.time = time;
			start();
		}

		void start() {
			taskId = Utils.repeat(0, 20, () -> {
				if (--time > 0) {
					MatchTimerTickEvent event = new MatchTimerTickEvent(match, time);
					Utils.callEvent(event);
					if (broadcasts.contains(time)) {
						match.broadcast("&e" + time + " &7seconds left...");
					}
				} else {
					match.end();
					stop();
				}
			});
		}

		void stop() {
			Utils.cancelTask(taskId);
		}

	}

}
