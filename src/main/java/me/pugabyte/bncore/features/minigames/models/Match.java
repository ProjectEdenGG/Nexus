package me.pugabyte.bncore.features.minigames.models;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchBroadcastEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.teams.TeamScoredEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.bncore.utils.BNScoreboard;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
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
	private MatchScoreboard scoreboard;
	private MatchData matchData;
	private MatchTasks tasks;

	public Optional<Minigamer> getMinigamer(Player player) {
		return minigamers.stream()
				.filter(minigamer -> minigamer.getPlayer().getUniqueId().equals(player.getUniqueId()))
				.findFirst();
	}

	public List<Player> getPlayers() {
		return minigamers.stream().map(Minigamer::getPlayer).collect(Collectors.toList());
	}

	public List<Team> getTeams() {
		return minigamers.stream().filter(minigamer -> minigamer.getTeam() != null).map(Minigamer::getTeam).collect(Collectors.toList());
	}

	public boolean join(Minigamer minigamer) {

		initialize();

		if (started) {
			if (arena.canJoinLate()) {
				if (!callJoinEvent(minigamer)) return false;
				minigamers.add(minigamer);
				balance();
				teleportIn(minigamer);
			} else {
				minigamer.tell("This match has already started");
				return false;
			}
		} else {
			if (!callJoinEvent(minigamer)) return false;
			minigamers.add(minigamer);
			arena.getLobby().join(minigamer);
		}

		arena.getMechanic().onJoin(minigamer);
		scoreboard.update();
		return true;
	}

	void quit(Minigamer minigamer) {
		if (!minigamers.contains(minigamer)) return;

		MatchQuitEvent event = new MatchQuitEvent(this, minigamer);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		minigamers.remove(minigamer);
		arena.getMechanic().onQuit(minigamer);
		minigamer.toGamelobby();
		minigamer.clearState();
		scoreboard.update();
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
		startTimer(); // -> arena.getMechanic().startTimer();
		arena.getMechanic().onStart(this);
		scoreboard.update();
	}

	public void end() {
		MatchEndEvent event = new MatchEndEvent(this);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		ended = true;
		tasks.end();
		broadcast("Match has ended");
		clearEntities();
		clearStates();
		toGamelobby();
		arena.getMechanic().onEnd(this);
		minigamers = new ArrayList<>();
		scoreboard.update();
		MatchManager.remove(this);
	}

	private boolean callJoinEvent(Minigamer minigamer) {
		MatchJoinEvent event = new MatchJoinEvent(this, minigamer);
		Utils.callEvent(event);
		return !event.isCancelled();
	}

	private void initialize() {
		if (!initialized) {
			arena.getMechanic().onInitialize(this);
			scoreboard = new MatchScoreboard(this);
			tasks = new MatchTasks();
			initialized = true;
		}
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

	private void toGamelobby() {
		minigamers.forEach(Minigamer::toGamelobby);
	}

	public void scored(Team team) {
		scored(team, 1);
	}

	public void scored(Team team, int score) {
		setScore(team, scores.get(team) + score);
	}

	public void setScore(Team team, int score) {
		int diff = score - scores.get(team);

		TeamScoredEvent event = new TeamScoredEvent(this, team, diff);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		scores.put(team, scores.get(team) + event.getAmount());
		scoreboard.update();
		if (scores.get(team) >= arena.getWinningScore())
			end();
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

	public List<Minigamer> getAlivePlayers() {
		return minigamers.stream().filter(Minigamer::isAlive).collect(Collectors.toList());
	}

	public class MatchTimer {
		private Match match;
		private List<Integer> broadcasts = Arrays.asList((60 * 10), (60 * 5), 60, 30, 15, 5, 4, 3, 2, 1);
		private int taskId;
		@Getter
		@Setter
		private int time;

		MatchTimer(Match match, int time) {
			this.match = match;
			this.time = time;
			start();
		}

		public void addTime(int time) {
			this.time += time;
		}

		void start() {
			taskId = match.getTasks().repeat(0, 20, () -> {
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

	public class MatchScoreboard {
		private Match match;
		private BNScoreboard scoreboard;

		public MatchScoreboard(Match match) {
			this.match = match;
			if (!match.getArena().hasScoreboard())
				return;
			scoreboard = new BNScoreboard(match.getArena().getMechanic().getScoreboardTitle(match));
			update();
		}

		public void update() {
			if (scoreboard == null)
				return;
			updatePlayers();
			scoreboard.setLines(match.getArena().getMechanic().getScoreboardLines(match));
		}

		private void updatePlayers() {
			for (Player player : Bukkit.getOnlinePlayers())
				if (!match.getPlayers().contains(player))
					scoreboard.removePlayer(player);

			scoreboard.addPlayers(match.getPlayers());
		}
	}

	public class MatchTasks {
		private List<Integer> taskIds = new ArrayList<>();

		void end() {
			taskIds.forEach(this::cancel);
		}

		public void cancel(int taskId) {
			Utils.cancelTask(taskId);
		}

		public int wait(long delay, Runnable runnable) {
			int taskId = Utils.wait(delay, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int repeat(long startDelay, long interval, Runnable runnable) {
			int taskId = Utils.repeat(startDelay, interval, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int async(Runnable runnable) {
			int taskId = Utils.async(runnable);
			taskIds.add(taskId);
			return taskId;
		}
	}

}
