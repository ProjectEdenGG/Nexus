package gg.projecteden.nexus.features.minigames.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.mechanics.Bingo;
import gg.projecteden.nexus.features.minigames.mechanics.Dropper;
import gg.projecteden.nexus.features.minigames.mechanics.Thimble;
import gg.projecteden.nexus.features.minigames.models.Match.MatchTasks.MatchTaskType;
import gg.projecteden.nexus.features.minigames.models.annotations.TeamGlowing;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBroadcastEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDisplayTimerEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerStartSpectatingEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.teams.TeamScoredEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import gg.projecteden.nexus.features.minigames.modifiers.NoModifier;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.BossBarBuilder;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.Countdown.CountdownBuilder;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.blastmc.holograms.api.models.Hologram;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
public class Match implements ForwardingAudience {
	@NonNull
	private Arena arena;
	private LocalDateTime created = LocalDateTime.now();
	@ToString.Exclude
	private final List<Minigamer> minigamers = new ArrayList<>();
	@ToString.Exclude
	private final List<Minigamer> spectators = new ArrayList<>();
	@ToString.Exclude
	private final List<Minigamer> allMinigamers = new ArrayList<>();
	private boolean initialized = false;
	private boolean started = false;
	private boolean begun = false;
	private boolean ended = false;
	private final Map<Team, Integer> scores = new HashMap<>();
	private MatchTimer timer;
	private MinigameScoreboard scoreboard;
	private final ArrayList<UUID> entityUuids = new ArrayList<>();
	private final ArrayList<Hologram> holograms = new ArrayList<>();
	private MatchData matchData;
	private MatchStatistics matchStatistics;
	private MatchTasks tasks;
	private final Set<Location> usedSpawnpoints = new HashSet<>();
	@Nullable
	private BossBarBuilder modifierBarBuilder;
	@Nullable
	private BossBar modifierBar;
	private final Map<Team, Integer> glowUpdates = new HashMap<>();

	public @Nullable Minigamer getMinigamer(@NotNull Player player) {
		for (Minigamer minigamer : minigamers)
			if (minigamer.getOnlinePlayer().equals(player))
				return minigamer;

		return null;
	}

	public List<Minigamer> getMinigamersAndSpectators() {
		List<Minigamer> players = new ArrayList<>();
		players.addAll(getMinigamers());
		players.addAll(getSpectators());
		return players;
	}

	public List<Minigamer> getOnlineMinigamersAndSpectators() {
		return getMinigamersAndSpectators().stream()
			.filter(Minigamer::isOnline)
			.collect(Collectors.toList());
	}

	/**
	 * Gets players who are currently in the match
	 *
	 * @return list of players
	 */
	public List<Player> getOnlinePlayers() {
		return minigamers.stream()
			.filter(Minigamer::isOnline)
			.map(Minigamer::getOnlinePlayer)
			.collect(Collectors.toList());
	}

	/**
	 * Gets all players who have joined the match
	 *
	 * @return list of offline players
	 */
	public List<OfflinePlayer> getAllPlayers() {
		return allMinigamers.stream()
			.map(Minigamer::getOfflinePlayer)
			.collect(Collectors.toList());
	}

	/**
	 * Gets all teams with living (i.e. not spectating) players.
	 *
	 * @return list of teams
	 */
	public List<Team> getAliveTeams() {
		// collects to a set first to remove duplicates
		return getAliveMinigamers().stream()
			.map(Minigamer::getTeam)
			.filter(Objects::nonNull)
			.distinct()
			.collect(Collectors.toList());
	}

	public List<Entity> getEntities() {
		return entityUuids.stream()
			.map(Bukkit::getEntity)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public <T extends Entity> List<T> getEntities(Class<T> clazz) {
		return getEntities().stream()
			.filter(entity -> clazz.isAssignableFrom(entity.getClass()))
			.map(entity -> (T) entity)
			.collect(Collectors.toList());
	}

	public <T extends Arena> T getArena() {
		return (T) arena;
	}

	public <T extends MatchData> T getMatchData() {
		return (T) matchData;
	}

	public <T extends MatchStatistics> T getMatchStatistics() {
		return (T) matchStatistics;
	}

	public <T extends Mechanic> T getMechanic() {
		return arena.getMechanic();
	}

	public World getWorld() {
		return arena.getWorld();
	}

	public WorldGuardUtils worldguard() {
		return arena.worldguard();
	}

	public WorldEditUtils worldedit() {
		return arena.worldedit();
	}

	public void checkCanJoin(Minigamer minigamer) {
		if ("RavensNestEstate".equals(arena.getName()))
			throw new InvalidInputException("&cThis arena is temporarily disabled while we work out some bugs");

		if (!initialized && Nexus.isMaintenanceQueued())
			throw new InvalidInputException("&cServer maintenance is queued, cannot start a new match");

		if (!initialize(minigamer))
			throw new InvalidInputException("&cMinigame initialization failed");

		if (started && !arena.canJoinLate())
			throw new InvalidInputException(new JsonBuilder("&cThis match has already started").next(getSpectatorHover(minigamer)));

		if (minigamers.size() >= arena.getMaxPlayers())
			throw new InvalidInputException(new JsonBuilder("&cThis match is full").next(getSpectatorHover(minigamer)));
	}

	public JsonBuilder getSpectatorHover(@NotNull Minigamer minigamer) {
		try {
			minigamer.checkCanSpectate(this, false);
			return new JsonBuilder(". &e&l[Spectate Instead]").command("mgm spectate " + getArena().getName()).hover("&3Spectate " + getArena().getName());
		}
		catch (InvalidInputException ignore) { return new JsonBuilder(); }
	}

	public void join(Minigamer minigamer) {
		if (minigamers.contains(minigamer))
			throw new InvalidInputException("&cYou are already in this match");

		MatchJoinEvent event = new MatchJoinEvent(this, minigamer);
		event.callEvent();

		minigamer.getOnlinePlayer().closeInventory();
		minigamers.add(minigamer);
		spectators.remove(minigamer);

		try {
			arena.getMechanic().processJoin(minigamer);
			arena.getMechanic().onJoin(event);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (scoreboard != null)
			scoreboard.handleJoin(minigamer);
	}

	void quit(Minigamer minigamer) {
		Minigames.debug("Minigamer#quit 1 " + minigamer.getNickname() + " online: " + minigamer.isOnline());
		if (!minigamers.contains(minigamer) && !spectators.contains(minigamer)) return;
		Minigames.debug("Minigamer#quit 2 " + minigamer.getNickname() + " online: " + minigamer.isOnline());

		minigamers.remove(minigamer);
		spectators.remove(minigamer);
		minigamer.clearState(true);
		minigamer.toGamelobby();

		MatchQuitEvent event = new MatchQuitEvent(minigamer);
		event.callEvent();

		if (!minigamer.isSpectating()) {
			try { arena.getMechanic().onQuit(event); }
			catch (Exception ex) { ex.printStackTrace(); }
		}

		if (modifierBar != null) minigamer.getOnlinePlayer().hideBossBar(modifierBar);
		if (scoreboard != null) scoreboard.handleQuit(minigamer);
		GlowUtils.unglow(getOnlinePlayers()).receivers(minigamer.getOnlinePlayer()).run();

		if (minigamers.size() == 0)
			end();
	}

	public void spectate(Minigamer minigamer) {
		if (minigamers.contains(minigamer) || spectators.contains(minigamer))
			throw new InvalidInputException("&cYou are already in this match");

		new MatchJoinEvent(this, minigamer).callEvent();

		minigamer.getOnlinePlayer().closeInventory();
		minigamer.setAlive(false);
		minigamer.setSpectating(true);
		spectators.add(minigamer);

		if (scoreboard != null)
			scoreboard.handleJoin(minigamer);

		new MinigamerStartSpectatingEvent(minigamer).callEvent();
		minigamer.toSpectate();
		broadcast("&e" + minigamer.getNickname() + " &3started spectating");
	}

	public void start() {
		if (started)
			throw new InvalidInputException("Match already started");

		MatchStartEvent event = new MatchStartEvent(this);
		if (!event.callEvent()) return;

		started = true;

		try {
			allMinigamers.addAll(minigamers);
			clearEntities();
			balance();
			initializeScores();
			teleportIn();
			startModifierBar();
			startTimer();
			arena.getMechanic().onStart(event);
			if (scoreboard != null) scoreboard.update();
		} catch (Exception ex) {
			ex.printStackTrace();
			end();
		}
	}

	public void end() {
		Minigames.debug("Match#end 1 " + getArena().getDisplayName());
		if (ended)
			return;

		Minigames.debug("Match#end 2 " + getArena().getDisplayName());
		MatchEndEvent event = new MatchEndEvent(this);
		if (!event.callEvent())
			return;

		Minigames.debug("Match#end 3 " + getArena().getDisplayName());
		ended = true;
		if (tasks != null)
			tasks.end();
		broadcast("Match has ended");
		logScores();
		broadcastNoPrefix("");
		clearHolograms();
		clearEntities();
		clearStates(true);
		stopModifierBar();
		toGamelobby();
		arena.getLobby().onEnd();
		try {
			arena.getMechanic().onEnd(event);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (scoreboard != null)
			scoreboard.handleEnd();

		List<Player> players = getOnlinePlayers();
		GlowUtils.unglow(players).receivers(players).run();

		minigamers.clear();
		spectators.clear();

		MatchManager.remove(this);
		Minigames.debug("Match#end 4 " + getArena().getDisplayName());
	}

	private void logScores() {
		if (!(getMechanic() instanceof Bingo))
			return;

		if (LocalDateTime.now().isAfter(LocalDateTime.of(2021, 7, 5, 12, 0, 0)))
			return;

		StringBuilder scores = new StringBuilder();
		if (arena.getMechanic() instanceof TeamMechanic) {
			getArena().getTeams().stream().sorted(Comparator.comparing((Team team) -> team.getScore(this)).reversed()).forEach(team ->
					scores.append("= ").append(team.getName()).append(" - ").append(team.getScore(this)).append(System.lineSeparator()));
		}

		getAllMinigamers().stream().sorted(Comparator.comparing(Minigamer::getScore).reversed()).forEach(minigamer ->
				scores.append("- ").append(minigamer.getNickname()).append(" - ").append(minigamer.getScore()).append(System.lineSeparator()));

		if (scores.length() > 0) {
			String header = "Scores for " + getArena().getName() + " (" + arena.getMechanic().getName() + "):" + System.lineSeparator();
			Discord.staffLog("```" + header + scores + "```");
		}
	}

	private boolean initialize(Minigamer debugger) {
		if (!initialized) {
			boolean errored = false;
			try {
				MatchInitializeEvent event = new MatchInitializeEvent(this);
				if (!event.callEvent())
					return false;

				initializeMatchData();
				initializeMatchStatistics();
				tasks = new MatchTasks();
				scoreboard = MinigameScoreboard.Factory.create(this);
				//scoreboardTeams = MinigameScoreboard.ITeams.Factory.create(this); // TODO: fix scoreboards
				arena.getMechanic().onInitialize(event);
				initialized = true;
			} catch (InvalidInputException ex) {
				errored = true;
				debugger.tell("&cError occurred: " + ex.getMessage());
			} catch (Exception ex) {
				errored = true;
				ex.printStackTrace();
			}

			if (errored) {
				end();
				return false;
			}
		}

		return true;
	}

	@SneakyThrows
	private void initializeMatchData() {
		Map<Mechanic, Constructor<?>> matchDataMap = Minigames.getMatchDataMap();
		if (matchDataMap.isEmpty())
			Minigames.registerMatchDatas();

		if (matchDataMap.containsKey(arena.getMechanic()))
			matchData = (MatchData) matchDataMap.get(arena.getMechanic()).newInstance(this);
		else
			matchData = new MatchData(this);
	}

	@SneakyThrows
	private void initializeMatchStatistics() {
		Map<Mechanic, Constructor<?>> matchStatisticsMap = Minigames.getMatchStatisticsMap();
		if (matchStatisticsMap.isEmpty())
			Minigames.registerMatchStatistics();

		if (matchStatisticsMap.containsKey(arena.getMechanic())) {
			Minigames.debug("Setting MatchStatistics class to " + matchStatisticsMap.get(arena.getMechanic()).newInstance(this.getMechanic().getMechanicType(), this).getClass().getSimpleName());
			matchStatistics = (MatchStatistics) matchStatisticsMap.get(arena.getMechanic()).newInstance(this.getMechanic().getMechanicType(), this);
		}
		else
			matchStatistics = new MatchStatistics(this.getMechanic().getMechanicType(), this);
	}

	private void startModifierBar() {
		MinigameModifier modifier = Minigames.getModifier();
		if (modifier instanceof NoModifier) return;

		modifierBarBuilder = new BossBarBuilder().title(modifier.asComponent()).color(BossBar.Color.BLUE);
		modifierBar = modifierBarBuilder.build();

		getMinigamersAndSpectators().stream()
			.filter(Minigamer::isOnline)
			.forEach(minigamer -> minigamer.getOnlinePlayer().showBossBar(modifierBar));
	}

	public void refreshModifierBar() {
		if (modifierBar == null) {
			startModifierBar();
			return;
		}

		MinigameModifier modifier = Minigames.getModifier();
		if (modifier instanceof NoModifier) {
			stopModifierBar();
			return;
		}

		if (modifierBar != null) {
			BossBarBuilder newBossBarBuilder = new BossBarBuilder().title(modifier.asComponent()).color(BossBar.Color.BLUE);
			if (newBossBarBuilder.getTitle().equals(modifierBarBuilder.getTitle()))
				return; // do nothing
		}

		stopModifierBar();
		startModifierBar();
	}

	private void stopModifierBar() {
		if (modifierBar == null) return;
		getMinigamersAndSpectators().stream()
			.filter(Minigamer::isOnline)
			.filter(player -> player.getPlayer() != null)
			.forEach(player -> player.getPlayer().hideBossBar(modifierBar));
	}

	private void startTimer() {
		timer = new MatchTimer(this, arena.getSeconds());
	}

	private void stopTimer() {
		if (timer != null)
			timer.stop();
	}

	public void handleGlow(Team team) {
		if (false && team != null && getMechanic().getAnnotation(TeamGlowing.class) != null && !glowUpdates.containsKey(team)) {
			// TODO: send potion effect packets instead of using GlowAPI (it creates its own scoreboard for colors which messes with things
			AtomicInteger taskId = new AtomicInteger(-1);
			taskId.set(tasks.wait(1, () -> {
				List<Player> teamMembers = team.getMinigamers(this).stream().map(Minigamer::getOnlinePlayer).collect(Collectors.toList());
				List<Player> otherPlayers = new ArrayList<>(OnlinePlayers.getAll());
				otherPlayers.removeAll(teamMembers);
				GlowUtils.glow(teamMembers).receivers(teamMembers).run();
				GlowUtils.unglow(otherPlayers).receivers(teamMembers).run();
				glowUpdates.remove(team, taskId.get());
			}));
			glowUpdates.put(team, taskId.get());
		}
	}

	private static List<EntityType> deletableTypes = List.of(EntityType.ARROW, EntityType.SPECTRAL_ARROW, EntityType.ITEM, EntityType.FALLING_BLOCK);

	private void clearEntities() {
		for (UUID uuid : entityUuids) {
			final Entity entity = getWorld().getEntity(uuid);
			if (entity != null)
				entity.remove();
		}

		getWorld().getEntities().forEach(entity -> {
			if (getArena().getRegion().contains(worldedit().toBlockVector3(entity.getLocation())))
				if (deletableTypes.contains(entity.getType()))
					entity.remove();
		});
	}

	public void clearHolograms() {
		holograms.forEach(Hologram::remove);
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
		if (isMechanic(Thimble.class) || isMechanic(Dropper.class) || isMechanic(Bingo.class)) return; // TODO Fix
		arena.getTeams().forEach(team -> team.spawn(this));
		spectators.forEach(Minigamer::toSpectate);
	}

	public void teleportIn(Minigamer minigamer) {
		if (minigamer.isSpectating())
			minigamer.toSpectate();
		else
			minigamer.getTeam().spawn(minigamer);
	}

	public void clearStates() {
		minigamers.forEach(Minigamer::clearState);
	}

	public void clearStates(boolean forceClearInventory) {
		getMinigamersAndSpectators().forEach(minigamer -> minigamer.clearState(forceClearInventory));
	}

	private void toGamelobby() {
		getMinigamersAndSpectators().forEach(Minigamer::toGamelobby);
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

		if (getMechanic().shouldBeOver(this))
			getMechanic().end(this);
	}

	public void broadcast(String message) {
		broadcast(new JsonBuilder(message));
	}

	public void broadcast(String message, MinigameMessageType type) {
		broadcast(new JsonBuilder(message), type);
	}

	public void broadcast(ComponentLike message) {
		if (Nullables.isNullOrEmpty(AdventureUtils.asPlainText(message))) {
			broadcastNoPrefix("");
			return;
		}

		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message);
		event.callEvent();
		if (!event.isCancelled())
			getMinigamersAndSpectators().forEach(minigamer -> minigamer.tell(event.getMessage()));
	}

	public void broadcast(ComponentLike message, MinigameMessageType type) {
		if (getMechanic().allowChat(type))
			broadcast(message);
	}

	public void broadcastNoPrefix(String message) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message);
		if (event.callEvent())
			getMinigamersAndSpectators().forEach(minigamer -> minigamer.sendMessage(event.getMessage()));
	}

	public void broadcastNoPrefix(String message, MinigameMessageType type) {
		if (getMechanic().allowChat(type))
			broadcastNoPrefix(message);
	}

	public void broadcast(Team team, String message) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message, team);
		if (event.callEvent())
			minigamers.stream()
				.filter(minigamer -> event.getTeam().equals(minigamer.getTeam()))
				.collect(Collectors.toSet())
				.forEach(minigamer -> minigamer.tell(event.getMessage()));
	}

	public void broadcast(Team team, String message, MinigameMessageType type) {
		if (getMechanic().allowChat(type))
			broadcast(team, message);
	}

	public void playSound(Jingle jingle) {
		getOnlinePlayers().forEach(jingle::play);
	}

	public List<Minigamer> getAliveMinigamers() {
		return minigamers.stream()
			.filter(Minigamer::isOnline)
			.filter(Minigamer::isAlive)
			.collect(Collectors.toList());
	}

	public List<Minigamer> getAliveMinigamers(Team team) {
		return getAliveMinigamers().stream()
			.filter(minigamer -> team.equals(minigamer.getTeam()))
			.collect(Collectors.toList());
	}

	public List<Minigamer> getOnlineMinigamers() {
		return minigamers.stream()
			.filter(Minigamer::isOnline)
			.collect(Collectors.toList());
	}

	public List<Minigamer> getDeadMinigamers() {
		return minigamers.stream()
			.filter(Minigamer::isDead)
			.collect(Collectors.toList());
	}

	public List<Player> getDeadOnlinePlayers() {
		return minigamers.stream()
			.filter(Minigamer::isDead)
			.map(Minigamer::getOnlinePlayer)
			.collect(Collectors.toList());
	}

	public List<Minigamer> getAliveMinigamersExcluding(List<Minigamer> minigamers) {
		return getAliveMinigamers().stream()
			.filter(minigamer -> !minigamers.contains(minigamer))
			.collect(Collectors.toList());
	}

	public List<Player> getAlivePlayers() {
		return minigamers.stream()
			.filter(Minigamer::isOnline)
			.filter(Minigamer::isAlive)
			.map(Minigamer::getOnlinePlayer)
			.collect(Collectors.toList());
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
		return mechanic.isAssignableFrom(getMechanic().getClass());
	}

	public Entity spawn(Location location, EntityType type) {
		return spawn(location, type.getEntityClass());
	}

	public <T extends Entity> T spawn(Location location, Class<T> type) {
		return spawn(location, type, null);
	}

	public <T extends Entity> T spawn(Location location, Class<T> type, Consumer<T> onSpawn) {
		T entity = location.getWorld().spawn(location, type);
		entityUuids.add(entity.getUniqueId());

		if (entity instanceof LivingEntity livingEntity)
			livingEntity.setRemoveWhenFarAway(false);

		if (onSpawn != null)
			onSpawn.accept(entity);

		return entity;
	}

	public int getWinningScore() {
		return arena.getCalculatedWinningScore(this);
	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		return getMinigamers();
	}

	public static class MatchTimer {
		private final Match match;
		private static final List<Integer> broadcasts = Arrays.asList((60 * 10), (60 * 5), 60, 30, 15, 5, 4, 3, 2, 1);
		private int taskId;
		@Getter
		@Setter
		@Accessors(chain = true)
		private int time;

		MatchTimer(Match match, int time) {
			this.match = match;
			this.time = time;
			start();
		}

		public void addTime(int time) {
			this.time += time;
		}

		public void start() {
			if (time > 0) {
				if (match.getMechanic().shouldBroadcastTimeLeft())
					match.getTasks().wait(1, () -> broadcastTimeLeft(time + 1));
				taskId = Tasks.repeat(0, TickTime.SECOND, () -> {
					new MatchTimerTickEvent(match, --time).callEvent();

					if (time > 0) {
						if (broadcasts.contains(time)) {
							if (match.getMechanic().shouldBroadcastTimeLeft()) {
								broadcastTimeLeft();
								match.getOnlineMinigamersAndSpectators().stream().map(Minigamer::getOnlinePlayer).forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, .75F, .6F));
							}
						}
						match.getOnlineMinigamersAndSpectators().forEach(minigamer -> {
							MinigamerDisplayTimerEvent event = new MinigamerDisplayTimerEvent(minigamer, time);
							if (event.callEvent())
								minigamer.sendActionBar(event.getContents());
						});
					} else {
						if (match.getMechanic().shouldAutoEndOnZeroTimeLeft())
							match.end();
						stop();
					}
				});
			} else {
				taskId = Tasks.repeat(0, TickTime.SECOND, () -> {
					MatchTimerTickEvent event = new MatchTimerTickEvent(match, ++time);
					event.callEvent();
				});
			}

			match.getTasks().register(MatchTaskType.MATCH_TIMER, taskId);
		}

		public void broadcastTimeLeft() {
			broadcastTimeLeft(time);
		}

		public void broadcastTimeLeft(int time) {
			match.getMechanic().broadcastTimeLeft(match, time);
		}

		public void stop() {
			Tasks.cancel(taskId);
		}
	}

	public static class MatchTasks {
		private final Set<Integer> taskIds = new HashSet<>();
		@Getter
		private final Map<MatchTaskType, Integer> taskTypeMap = new ConcurrentHashMap<>();

		void end() {
			List<Integer> tasks = new ArrayList<>(taskIds);
			tasks.forEach(this::cancel);
		}

		public void cancel(int taskId) {
			Tasks.cancel(taskId);
			taskIds.remove(taskId);
		}

		/**
		 * Cancels a task. If <code>taskId</code> is not <code>-1</code>, the corresponding task will be cancelled, and
		 * the <code>taskType</code> will be removed from the internal map if its saved task id matches.
		 * <p>
		 * If <code>taskId</code> is -1, the saved task for <code>taskType</code> will be removed and cancelled
		 * (if present).
		 * @param taskType a match task type
		 * @param taskId a task id or -1
		 */
		public void cancel(@NotNull MatchTaskType taskType, int taskId) {
			if (taskId != -1) {
				Tasks.cancel(taskId);
				taskTypeMap.remove(taskType, taskId);
			} else if (taskTypeMap.containsKey(taskType))
				Tasks.cancel(taskTypeMap.remove(taskType));
		}

		public void cancel(MatchTaskType taskType) {
			cancel(taskType, -1);
		}

		public void register(int taskId) {
			taskIds.add(taskId);
		}

		public void register(MatchTaskType taskType, int taskId) {
			cancel(taskType);
			taskTypeMap.put(taskType, taskId);
			register(taskId);
		}

		public boolean registered(MatchTaskType taskType) {
			return taskTypeMap.containsKey(taskType);
		}

		public int wait(TickTime delay, Runnable runnable) {
			return wait(delay.get(), runnable);
		}

		public int wait(long delay, Runnable runnable) {
			int taskId = Tasks.wait(delay, runnable);
			taskIds.add(taskId);
			Tasks.wait(delay, () -> taskIds.remove(taskId));
			return taskId;
		}

		public int waitAsync(TickTime delay, Runnable runnable) {
			return waitAsync(delay.get(), runnable);
		}

		public int waitAsync(long delay, Runnable runnable) {
			int taskId = Tasks.waitAsync(delay, runnable);
			taskIds.add(taskId);
			Tasks.wait(delay, () -> taskIds.remove(taskId));
			return taskId;
		}

		public int repeat(TickTime startDelay, long interval, Runnable runnable) {
			return repeat(startDelay.get(), interval, runnable);
		}

		public int repeat(long startDelay, TickTime interval, Runnable runnable) {
			return repeat(startDelay, interval.get(), runnable);
		}

		public int repeat(TickTime startDelay, TickTime interval, Runnable runnable) {
			return repeat(startDelay.get(), interval.get(), runnable);
		}

		public int repeat(long startDelay, long interval, Runnable runnable) {
			int taskId = Tasks.repeat(startDelay, interval, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int repeatAsync(TickTime startDelay, long interval, Runnable runnable) {
			return repeatAsync(startDelay.get(), interval, runnable);
		}

		public int repeatAsync(long startDelay, TickTime interval, Runnable runnable) {
			return repeatAsync(startDelay, interval.get(), runnable);
		}

		public int repeatAsync(TickTime startDelay, TickTime interval, Runnable runnable) {
			return repeatAsync(startDelay.get(), interval.get(), runnable);
		}

		public int repeatAsync(long startDelay, long interval, Runnable runnable) {
			int taskId = Tasks.repeatAsync(startDelay, interval, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int sync(Runnable runnable) {
			int taskId = Tasks.sync(runnable);
			taskIds.add(taskId);
			Tasks.sync(() -> taskIds.remove(taskId));
			return taskId;
		}

		public int async(Runnable runnable) {
			int taskId = Tasks.async(runnable);
			taskIds.add(taskId);
			Tasks.async(() -> taskIds.remove(taskId));
			return taskId;
		}

		public int countdown(CountdownBuilder countdown) {
			int taskId = countdown.start().getTaskId();
			taskIds.add(taskId);
			return taskId;
		}

		public enum MatchTaskType {
			MATCH,
			MATCH_TIMER,
			LOBBY,
			BEGIN_DELAY,
			SCOREBOARD,
			TURN,
			TURN_TIMER,
			TICK
		}
	}

}
