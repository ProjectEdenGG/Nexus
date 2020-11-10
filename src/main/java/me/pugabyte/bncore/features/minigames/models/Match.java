package me.pugabyte.bncore.features.minigames.models;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.mechanics.UncivilEngineers;
import me.pugabyte.bncore.features.minigames.models.Match.MatchTasks.MatchTaskType;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchBroadcastEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.teams.TeamScoredEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.bncore.features.minigames.models.scoreboards.MinigameScoreboard;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.ActionBarUtils;
import me.pugabyte.bncore.utils.StringUtils.TimespanFormatType;
import me.pugabyte.bncore.utils.StringUtils.TimespanFormatter;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Tasks.Countdown.CountdownBuilder;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
public class Match {
	@NonNull
	private Arena arena;
	@ToString.Exclude
	private List<Minigamer> minigamers = new ArrayList<>();
	@ToString.Exclude
	private List<Minigamer> allMinigamers = new ArrayList<>();
	private boolean initialized = false;
	private boolean started = false;
	private boolean ended = false;
	private Map<Team, Integer> scores = new HashMap<>();
	private MatchTimer timer;
	private MinigameScoreboard scoreboard;
	private MinigameScoreboard.Teams scoreboardTeams;
	private ArrayList<Entity> entities = new ArrayList<>();
	private ArrayList<Hologram> holograms = new ArrayList<>();
	private MatchData matchData;
	private MatchTasks tasks;

	public Minigamer getMinigamer(Player player) {
		for (Minigamer minigamer : minigamers)
			if (minigamer.getPlayer().equals(player))
				return minigamer;

		return null;
	}

	public List<Player> getPlayers() {
		return minigamers.stream().map(Minigamer::getPlayer).collect(Collectors.toList());
	}

	public List<Player> getAllPlayers() {
		return allMinigamers.stream().map(Minigamer::getPlayer).collect(Collectors.toList());
	}

	public List<Team> getAliveTeams() {
		return getAliveMinigamers().stream().filter(minigamer -> minigamer.getTeam() != null).map(Minigamer::getTeam).collect(Collectors.toList());
	}

	public <T extends Arena> T getArena() {
		return (T) arena;
	}

	public <T extends MatchData> T getMatchData() {
		return (T) matchData;
	}

	public World getWorld() {
		return arena.getWorld();
	}

	public WorldGuardUtils getWGUtils() {
		return arena.getWGUtils();
	}

	public WorldEditUtils getWEUtils() {
		return arena.getWEUtils();
	}

	public boolean join(Minigamer minigamer) {
		List<Class<?>> usesWorldEdit = Arrays.asList(UncivilEngineers.class);
		if (usesWorldEdit.contains(arena.getMechanic().getClass()) || arena.getName().equals("RavensNestEstate")) {
			minigamer.tell("This arena is temporarily disabled while we work out some bugs");
			return false;
		}

		initialize();

		if (started && !arena.canJoinLate()) {
			minigamer.tell("This match has already started");
			return false;
		}

		if (minigamers.size() >= arena.getMaxPlayers()) {
			minigamer.tell("This match is full");
			return false;
		}

		MatchJoinEvent event = new MatchJoinEvent(this, minigamer);
		if (!event.callEvent()) return false;

		minigamers.add(minigamer);
		allMinigamers.add(minigamer);

		try {
			arena.getMechanic().processJoin(minigamer);
			arena.getMechanic().onJoin(event);
		} catch (Exception ex) { ex.printStackTrace(); }

		if (scoreboard != null) scoreboard.handleJoin(minigamer);
		if (scoreboardTeams != null) scoreboardTeams.handleJoin(minigamer);

		return true;
	}

	void quit(Minigamer minigamer) {
		if (!minigamers.contains(minigamer)) return;

		MatchQuitEvent event = new MatchQuitEvent(this, minigamer);
		event.callEvent();
		if (event.isCancelled()) return;

		minigamers.remove(minigamer);
		try { arena.getMechanic().onQuit(event); } catch (Exception ex) { ex.printStackTrace(); }
		minigamer.clearState();
		minigamer.toGamelobby();
		minigamer.unhideAll();

		if (scoreboard != null) scoreboard.handleQuit(minigamer);
		if (scoreboardTeams != null) scoreboardTeams.handleQuit(minigamer);

		if (minigamers == null || minigamers.size() == 0)
			end();
	}

	public void start() {
		if (started)
			throw new InvalidInputException("Match already started");

		MatchStartEvent event = new MatchStartEvent(this);
		if (!event.callEvent()) return;

		started = true;

		try {
			clearEntities();
			balance();
			initializeScores();
			teleportIn();
			startTimer(); // -> arena.getMechanic().startTimer();
			arena.getMechanic().onStart(event);

			if (scoreboard != null) scoreboard.update();
			if (scoreboardTeams != null) scoreboardTeams.update();
		} catch (Exception ex) {
			ex.printStackTrace();
			end();
		}
	}

	public void end() {
		if (ended) return;

		MatchEndEvent event = new MatchEndEvent(this);
		if (!event.callEvent()) return;

		ended = true;
		if (tasks != null)
			tasks.end();
		broadcast("Match has ended");
		//logScores();
		broadcastNoPrefix("");
		clearHolograms();
		clearEntities();
		clearStates();
		toGamelobby();
		arena.getMechanic().onEnd(event);
		minigamers.clear();

		if (scoreboard != null) scoreboard.handleEnd();
		if (scoreboardTeams != null) scoreboardTeams.handleEnd();

		MatchManager.remove(this);
	}

	private void logScores() {
		StringBuilder scores = new StringBuilder();
		if (arena.getMechanic() instanceof TeamMechanic) {
			getArena().getTeams().stream().sorted(Comparator.comparing((Team team) -> team.getScore(this)).reversed()).forEach(team ->
					scores.append("= ").append(team.getName()).append(" - ").append(team.getScore(this)).append(System.lineSeparator()));
		}

		getAllMinigamers().stream().sorted(Comparator.comparing(Minigamer::getScore).reversed()).forEach(minigamer ->
				scores.append("- ").append(minigamer.getName()).append(" - ").append(minigamer.getScore()).append(System.lineSeparator()));

		if (scores.length() > 0) {
			String header = "Scores for " + getArena().getName() + " (" + arena.getMechanic().getName() + "):" + System.lineSeparator();
			Discord.staffLog("```" + header + scores.toString() + "```");
		}
	}

	private void initialize() {
		if (!initialized) {
			try {
				MatchInitializeEvent event = new MatchInitializeEvent(this);
				if (!event.callEvent()) return;

				initializeMatchData();
				tasks = new MatchTasks();
				scoreboard = MinigameScoreboard.Factory.create(this);
				scoreboardTeams = MinigameScoreboard.Teams.Factory.create(this);
				arena.getMechanic().onInitialize(event);
				initialized = true;
			} catch (Exception ex) {
				ex.printStackTrace();
				end();
			}
		}
	}

	@SneakyThrows
	private void initializeMatchData() {
		String path = this.getClass().getPackage().getName();
		Set<Class<? extends MatchData>> matchDataTypes = new Reflections(path + ".matchdata")
				.getSubTypesOf(MatchData.class);

		matchDataTypes:
		for (Class<?> matchDataType : matchDataTypes)
			for (Class<? extends Mechanic> superclass : arena.getMechanic().getSuperclasses())
				if (matchDataType.getAnnotation(MatchDataFor.class) != null)
					if (matchDataType.getAnnotation(MatchDataFor.class).value().equals(superclass)) {
						matchData = (MatchData) matchDataType.getConstructor(Match.class).newInstance(this);
						break matchDataTypes;
					}
	}

	private void startTimer() {
		timer = new MatchTimer(this, arena.getSeconds());
	}

	private void stopTimer() {
		if (timer != null)
			timer.stop();
	}

	private static List<EntityType> deletableTypes = Arrays.asList(EntityType.ARROW, EntityType.SPECTRAL_ARROW, EntityType.DROPPED_ITEM);

	private void clearEntities() {
		entities.forEach(Entity::remove);
		getWorld().getEntities().forEach(entity -> {
			if (getArena().getRegion().contains(getWEUtils().toBlockVector3(entity.getLocation())))
				if (deletableTypes.contains(entity.getType()))
					entity.remove();
		});
	}

	public void clearHolograms() {
		holograms.forEach(Hologram::delete);
		holograms.clear();
	}

	private void balance() {
		arena.getMechanic().balance(minigamers);
		if (arena.getMechanic() instanceof TeamMechanic)
			minigamers.forEach(minigamer -> minigamer.tell("You are on team " + minigamer.getTeam().getColoredName()));
	}

	private void initializeScores() {
		arena.getTeams().forEach(team -> scores.put(team, 0));
	}

	private void teleportIn() {
		arena.getTeams().forEach(team -> team.spawn(minigamers));
	}

	public void teleportIn(Minigamer minigamer) {
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
		setScore(team, scores.getOrDefault(team, 0) + score);
	}

	public void setScore(Team team, int score) {
		int diff = score - scores.getOrDefault(team, 0);

		TeamScoredEvent event = new TeamScoredEvent(this, team, diff);
		if (!event.callEvent()) return;

		scores.put(team, scores.getOrDefault(team, 0) + event.getAmount());
		scoreboard.update();
	}

	public void broadcast(String message) {
		if (Strings.isNullOrEmpty(message)) {
			broadcastNoPrefix("");
			return;
		}

		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message);
		event.callEvent();
		if (!event.isCancelled())
			minigamers.forEach(minigamer -> minigamer.tell(colorize(event.getMessage())));
	}

	public void broadcastNoPrefix(String message) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message);
		if (event.callEvent())
			minigamers.forEach(minigamer -> minigamer.send(colorize(event.getMessage())));
	}

	public void broadcast(String message, Team team) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message, team);
		if (event.callEvent()) {
			minigamers.stream()
					.filter(minigamer -> minigamer.getTeam().equals(event.getTeam()))
					.collect(Collectors.toSet())
					.forEach(minigamer -> minigamer.tell(colorize(event.getMessage())));
		}
	}

	public List<Minigamer> getAliveMinigamers() {
		return minigamers.stream().filter(Minigamer::isAlive).collect(Collectors.toList());
	}

	public List<Player> getAlivePlayers() {
		return minigamers.stream().filter(Minigamer::isAlive).map(Minigamer::getPlayer).collect(Collectors.toList());
	}

	public List<Minigamer> getUnassignedPlayers() {
		return minigamers.stream()
				.filter(waiting -> waiting.getTeam() == null)
				.collect(Collectors.toList());
	}

	public boolean isMechanic(Mechanic mechanic) {
		return isMechanic(mechanic.getClass());
	}

	public boolean isMechanic(Class<? extends Mechanic> mechanic) {
		return mechanic.isInstance(getArena().getMechanic());
	}

	public <T extends Entity> T spawn(Location location, Class<T> type) {
		T entity = location.getWorld().spawn(location, type);
		entities.add(entity);
		return entity;
	}

	public static class MatchTimer {
		private final Match match;
		private static final List<Integer> broadcasts = Arrays.asList((60 * 10), (60 * 5), 60, 30, 15, 5, 4, 3, 2, 1);
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
			if (time > 0) {
				match.getTasks().wait(1, () -> broadcastTimeLeft(time + 1));
				taskId = match.getTasks().repeat(0, Time.SECOND, () -> {
					if (--time > 0) {
						if (broadcasts.contains(time)) {
							broadcastTimeLeft();
							match.getPlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, .75F, .6F));
						}
						match.getPlayers().forEach(player -> ActionBarUtils.sendActionBar(player, TimespanFormatter.of(time).format(), 2, false));
					} else {
						match.end();
						stop();
					}
				});
			} else {
				taskId = match.getTasks().repeat(0, Time.SECOND, () -> {
					MatchTimerTickEvent event = new MatchTimerTickEvent(match, ++time);
					event.callEvent();
				});
			}

			match.getTasks().register(MatchTaskType.MATCH, taskId);
		}

		public void broadcastTimeLeft() {
			broadcastTimeLeft(time);
		}

		public void broadcastTimeLeft(int time) {
			match.broadcast("&e" + TimespanFormatter.of(time).formatType(TimespanFormatType.LONG).format() + " &7left...");
		}

		void stop() {
			Tasks.cancel(taskId);
		}
	}

	public static class MatchTasks {
		private final List<Integer> taskIds = new ArrayList<>();
		@Getter
		private final Map<MatchTaskType, Integer> taskTypeMap = new HashMap<>();

		void end() {
			taskIds.forEach(this::cancel);
		}

		public void cancel(int taskId) {
			Tasks.cancel(taskId);
		}

		public void cancel(MatchTaskType taskType) {
			if (!taskTypeMap.containsKey(taskType))
				return;

			Tasks.cancel(taskTypeMap.get(taskType));
			taskTypeMap.remove(taskType);
		}

		public void register(int taskId) {
			taskIds.add(taskId);
		}

		public void register(MatchTaskType taskType, int taskId) {
			taskTypeMap.put(taskType, taskId);
		}

		public int wait(Time delay, Runnable runnable) {
			return wait(delay.get(), runnable);
		}

		public int wait(long delay, Runnable runnable) {
			int taskId = Tasks.wait(delay, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int waitAsync(Time delay, Runnable runnable) {
			return waitAsync(delay.get(), runnable);
		}

		public int waitAsync(long delay, Runnable runnable) {
			int taskId = Tasks.waitAsync(delay, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int repeat(Time startDelay, long interval, Runnable runnable) {
			return repeat(startDelay.get(), interval, runnable);
		}

		public int repeat(long startDelay, Time interval, Runnable runnable) {
			return repeat(startDelay, interval.get(), runnable);
		}

		public int repeat(Time startDelay, Time interval, Runnable runnable) {
			return repeat(startDelay.get(), interval.get(), runnable);
		}

		public int repeat(long startDelay, long interval, Runnable runnable) {
			int taskId = Tasks.repeat(startDelay, interval, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int async(Runnable runnable) {
			int taskId = Tasks.async(runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int countdown(CountdownBuilder countdown) {
			int taskId = countdown.start().getTaskId();
			taskIds.add(taskId);
			return taskId;
		}

		public enum MatchTaskType {
			MATCH,
			LOBBY,
			BEGIN_DELAY,
			SCOREBOARD;
		}
	}

}
