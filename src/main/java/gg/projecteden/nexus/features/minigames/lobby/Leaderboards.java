package gg.projecteden.nexus.features.minigames.lobby;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService.LeaderboardRanking;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsUser;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.events.HologramInteractEvent;
import tech.blastmc.holograms.api.events.HologramLineSpawnEvent;
import tech.blastmc.holograms.api.events.HologramSpawnEvent;
import tech.blastmc.holograms.api.models.Hologram;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Leaderboards {

	public Leaderboards() {
		Tasks.sync(() -> {
			new MGMLeaderboard(MatchStatistics.WINS);
			new MGMLeaderboard(MatchStatistics.GAMES_PLAYED);
			new MGMLeaderboard(PVPStats.KILLS);
			new MGMLeaderboard("leaderboard_misc");
		});
	}

	public static class MGMLeaderboard implements Listener {

		private static final MinigameStatsService SERVICE = new MinigameStatsService();
		private final MinigameStatistic statistic;
		private final Hologram hologram;
		private Hologram mechanicHologram;
		private Hologram statisticHologram;
		private final boolean hasControls;
		private final Map<UUID, List<String>> lines = new HashMap<>();

		public MGMLeaderboard(MinigameStatistic statistic) {
			this(statistic, "leaderboard_" + statistic.getId(), false);
		}

		public MGMLeaderboard(String hologramId) {
			this(null, hologramId, true);
		}

		private MGMLeaderboard(MinigameStatistic statistic, String hologramId, boolean hasControls) {
			this.statistic = statistic;
			this.hologram = HologramsAPI.byId(Minigames.getWorld(), hologramId);
			this.hasControls = hasControls;

			if (this.hologram == null)
				if (this.statistic != null)
					throw new InvalidInputException("Could not find leaderboard for '" + statistic.getId() + "'");
				else
					throw new InvalidInputException("Could not find hologram for leaderboard with id '" + hologramId + "'");

			if (this.hasControls) {
				this.mechanicHologram = HologramsAPI.byId(Minigames.getWorld(), this.hologram.getId() + "_mechanic");
				this.statisticHologram = HologramsAPI.byId(Minigames.getWorld(), this.hologram.getId() + "_statistic");

				if (this.mechanicHologram == null)
					throw new InvalidInputException("Could not find mechanic controls for '" + this.hologram.getId() + "'");
				if (this.statisticHologram == null)
					throw new InvalidInputException("Could not find statistic controls for '" + this.hologram.getId() + "'");
			}

			Nexus.registerListener(this);

			/*
			 * The holograms api isn't really setup for this and would require major refactoring to get it to work
			 * As such, we have the 'fake' the per-player lines, as it expects the same number for each player
			 * Luckily this will be fine for these, but we will need something else for the ones requiring different counts
			 */
			Tasks.wait(5, () -> {
				this.hologram.setInteractable(true);
				this.hologram.setLines(new ArrayList<>(Collections.nCopies(17, ".")));
				this.hologram.setBackground(Color.fromARGB(170, 0, 0, 0));

				if (this.hasControls) {
					this.mechanicHologram.setInteractable(true);
					this.mechanicHologram.setLines(new ArrayList<>(Collections.nCopies(9, ".")));
					this.mechanicHologram.setBackground(Color.fromARGB(170, 0, 0, 0));

					this.statisticHologram.setInteractable(true);
					this.statisticHologram.setLines(new ArrayList<>(Collections.nCopies(9, ".")));
					this.statisticHologram.setBackground(Color.fromARGB(170, 0, 0, 0));
				}
			});
		}

		@EventHandler
		public void onSpawnGenerateLines(HologramSpawnEvent event) {
			if (!event.getHologram().getLocation().getWorld().getName().equals(Minigames.getWorld().getName()))
				return;
			if (!event.getHologram().getId().equals(this.hologram.getId()))
				return;

			setup(event.getPlayer());
		}

		private void setup(Player player) {
			MinigameStatsUser user = SERVICE.get(player);
			MechanicType mechanicType = user.getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), this.hasControls ? MechanicType.ARCHERY : null);
			DateRange dateRange = user.getLeaderboardDateRanges().getOrDefault(this.hologram.getId(), DateRange.ALL_TIME);

			List<LeaderboardRanking> rankings = SERVICE.getLeaderboard(mechanicType, getStatistic(player), dateRange.getDate());

			List<String> lines = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				if (rankings.size() > i) {
					LeaderboardRanking ranking = rankings.get(i);
					lines.add("&6%d. %s &7- &7%s".formatted(ranking.getRank(), Nerd.of(ranking.getUuid()).getColoredName(), ranking.getScore()));
				}
				else {
					lines.add("&6%d. &7-".formatted(i + 1));
				}
			}

			LeaderboardRanking self = rankings.stream().filter(rank -> rank.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
			if (self != null)
				lines.add("&6%d. %s &7(YOU) &7- &7%s".formatted(self.getRank(), Nerd.of(self.getUuid()).getColoredName(), self.getScore()));
			else
				lines.add("&7You have no data");

			this.lines.put(player.getUniqueId(), lines);
		}

		private MinigameStatistic getStatistic(Player player) {
			if (this.statistic != null)
				return this.statistic;

			MinigameStatsUser user = SERVICE.get(player);
			List<MinigameStatistic> stats = getStatistics(player);

			if (user.getLeaderboardStatistics().containsKey(this.hologram.getId())) {
				String stat = user.getLeaderboardStatistics().get(this.hologram.getId());
				MinigameStatistic minigameStatistic = stats.stream().filter(_stat -> _stat.getId().equals(stat)).findFirst().orElse(null);
				if (minigameStatistic != null)
					return minigameStatistic;

				// no matching, so we've swapped mechanics (clear)
				user.getLeaderboardStatistics().remove(this.hologram.getId());
				SERVICE.save(user);
			}

			stats = stats.stream().sorted(Comparator.comparing(stat -> stat.getTitle())).toList();
			return stats.getFirst();
		}

		private List<MinigameStatistic> getStatistics(Player player) {
			MinigameStatsUser user = SERVICE.get(player);
			MechanicType mechanicType = user.getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), MechanicType.ARCHERY);

			return new ArrayList<>(mechanicType.getStatistics().stream().filter(stat -> !stat.isHidden()).toList());
		}

		private String getMechanicName(Player player) {
			if (this.statistic != null)
				return "Global";

			MinigameStatsUser user = SERVICE.get(player);
			MechanicType mechanicType = user.getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), MechanicType.ARCHERY);

			return StringUtils.camelCase(mechanicType);
		}

		@EventHandler
		public void onLineSpawn(HologramLineSpawnEvent event) {
			if (!event.getHologram().getLocation().getWorld().getName().equals(Minigames.getWorld().getName()))
				return;
			if (!event.getHologram().getId().equals(this.hologram.getId()))
				return;

			if (!this.lines.containsKey(event.getPlayer().getUniqueId()))
				setup(event.getPlayer());

			Object data = switch (event.getLine().getIndex()) {
				case 0: yield "&6&l" + getStatistic(event.getPlayer()).getTitle() + " (%s)".formatted(getMechanicName(event.getPlayer()));
				case 1:
				case 14:
					yield "&f";
				case 12: yield "&7...";
				case 16: {
					if (this.hasControls)
						if (SERVICE.get(event.getPlayer()).getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), MechanicType.ARCHERY).get() instanceof CheckpointMechanic)
							yield "&7Time Controls not available";
					yield "&7← Shift Click | Click →";
				}
				case 15: {
					MinigameStatsUser user = SERVICE.get(event.getPlayer());
					DateRange range = user.getLeaderboardDateRanges().getOrDefault(this.hologram.getId(), DateRange.ALL_TIME);

					boolean timed = user.getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), MechanicType.ARCHERY).get() instanceof CheckpointMechanic;

					yield
						"&7" + StringUtils.camelCase(EnumUtils.previousWithLoop(DateRange.class, range.ordinal()).name()) +
						" &6| " +
						(timed ? "&7" : "&3&l") + StringUtils.camelCase(range) +
						" &6| " +
						"&7" + StringUtils.camelCase(EnumUtils.nextWithLoop(DateRange.class, range.ordinal()).name());
				}
				case 13: yield this.lines.get(event.getPlayer().getUniqueId()).get(10);
				default: yield this.lines.get(event.getPlayer().getUniqueId()).get(event.getLine().getIndex() - 2);
			};
			event.setData(data);
		}

		@EventHandler
		public void onLineSpawnMechanic(HologramLineSpawnEvent event) {
			if (!this.hasControls)
				return;

			if (!event.getHologram().getLocation().getWorld().getName().equals(Minigames.getWorld().getName()))
				return;
			if (!event.getHologram().getId().equals(this.mechanicHologram.getId()))
				return;

			MinigameStatsUser user = SERVICE.get(event.getPlayer());
			MechanicType mechanicType = user.getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), MechanicType.ARCHERY);

			List<MechanicType> types = ArenaManager.getAllEnabled().stream().map(Arena::getMechanicType)
				.distinct().filter(MechanicType::isEnabled)
				.sorted(Comparator.comparing(Enum::name))
				.toList();

			int index = event.getLine().getIndex();
			Object data = switch (index) {
				case 0: yield "&6&lMechanic";
				case 2: yield "&7" + getPreviousWithLoop(types, types.indexOf(getPreviousWithLoop(types, types.indexOf(mechanicType)))).get().getName();
				case 3: yield "&7" + getPreviousWithLoop(types, types.indexOf(mechanicType)).get().getName();
				case 4: yield "&3" + mechanicType.get().getName();
				case 5: yield "&7" + getNextWithLoop(types, types.indexOf(mechanicType)).get().getName();
				case 6: yield "&7" + getNextWithLoop(types, types.indexOf(getNextWithLoop(types, types.indexOf(mechanicType)))).get().getName();
				case 8: yield "&7↑ Shift Click | Click ↓";
				default: yield "&f";
			};
			event.setData(data);
		}

		@EventHandler
		public void onLineSpawnStatistic(HologramLineSpawnEvent event) {
			if (!this.hasControls)
				return;

			if (!event.getHologram().getLocation().getWorld().getName().equals(Minigames.getWorld().getName()))
				return;
			if (!event.getHologram().getId().equals(this.statisticHologram.getId()))
				return;

			List<MinigameStatistic> stats = getStatistics(event.getPlayer());
			MinigameStatistic stat = getStatistic(event.getPlayer());

			MinigameStatsUser user = SERVICE.get(event.getPlayer());
			boolean timed = user.getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), MechanicType.ARCHERY).get() instanceof CheckpointMechanic;

			int index = event.getLine().getIndex();
			Object data = switch (index) {
				case 0: yield timed ? "&6&lArena" : "&6&lStatistic";
				case 2: yield "&7" + getPreviousWithLoop(stats, stats.indexOf(getPreviousWithLoop(stats, stats.indexOf(stat)))).getTitle();
				case 3: yield "&7" + getPreviousWithLoop(stats, stats.indexOf(stat)).getTitle();
				case 4: yield "&3" + stat.getTitle();
				case 5: yield "&7" + getNextWithLoop(stats, stats.indexOf(stat)).getTitle();
				case 6: yield "&7" + getNextWithLoop(stats, stats.indexOf(getNextWithLoop(stats, stats.indexOf(stat)))).getTitle();
				case 8: yield "&7↑ Shift Click | Click ↓";
				default: yield "&f";
			};
			event.setData(data);
		}

		@EventHandler
		public void onInteract(HologramInteractEvent event) {
			if (!event.getHologram().getLocation().getWorld().getName().equals(Minigames.getWorld().getName()))
				return;
			if (!event.getHologram().getId().equals(this.hologram.getId()))
				return;

			boolean reverse = event.getPlayer().isSneaking();

			MinigameStatsUser user = SERVICE.get(event.getPlayer());

			boolean timed = user.getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), MechanicType.ARCHERY).get() instanceof CheckpointMechanic;
			if (timed)
				return;

			DateRange range = user.getLeaderboardDateRanges().getOrDefault(this.hologram.getId(), DateRange.ALL_TIME);

			DateRange newRange = reverse ? EnumUtils.previousWithLoop(DateRange.class, range.ordinal()) : EnumUtils.nextWithLoop(DateRange.class, range.ordinal());
			user.getLeaderboardDateRanges().put(this.hologram.getId(), newRange);
			SERVICE.save(user);

			this.update(event.getPlayer());
		}

		@EventHandler
		public void onInteractMechanic(HologramInteractEvent event) {
			if (!this.hasControls)
				return;

			if (!event.getHologram().getLocation().getWorld().getName().equals(Minigames.getWorld().getName()))
				return;
			if (!event.getHologram().getId().equals(this.mechanicHologram.getId()))
				return;

			boolean reverse = event.getPlayer().isSneaking();

			MinigameStatsUser user = SERVICE.get(event.getPlayer());
			MechanicType mechanicType = user.getLeaderboardMechanicTypes().getOrDefault(this.hologram.getId(), MechanicType.ARCHERY);

			List<MechanicType> types = ArenaManager.getAllEnabled().stream().map(Arena::getMechanicType)
				.distinct().filter(MechanicType::isEnabled)
				.sorted(Comparator.comparing(Enum::name))
				.toList();

			mechanicType = reverse ? getPreviousWithLoop(types, types.indexOf(mechanicType)) : getNextWithLoop(types, types.indexOf(mechanicType));

			user.getLeaderboardMechanicTypes().put(this.hologram.getId(), mechanicType);
			SERVICE.save(user);

			this.update(event.getPlayer());
		}

		@EventHandler
		public void onInteractStatistic(HologramInteractEvent event) {
			if (!this.hasControls)
				return;

			if (!event.getHologram().getLocation().getWorld().getName().equals(Minigames.getWorld().getName()))
				return;
			if (!event.getHologram().getId().equals(this.statisticHologram.getId()))
				return;

			boolean reverse = event.getPlayer().isSneaking();

			MinigameStatsUser user = SERVICE.get(event.getPlayer());
			List<MinigameStatistic> stats = getStatistics(event.getPlayer());
			MinigameStatistic stat = getStatistic(event.getPlayer());

			stat = reverse ? getPreviousWithLoop(stats, stats.indexOf(stat)) : getNextWithLoop(stats, stats.indexOf(stat));

			user.getLeaderboardStatistics().put(this.hologram.getId(), stat.getId());
			SERVICE.save(user);

			this.update(event.getPlayer());
		}

		private final Map<UUID, Integer> taskIds = new HashMap<>();

		private void update(Player player) {
			if (taskIds.containsKey(player.getUniqueId()))
				Tasks.cancel(taskIds.get(player.getUniqueId()));

			// Update the DateRange immediately, but the other lines are cached
			this.hologram.showToPlayer(player);
			taskIds.put(player.getUniqueId(), Tasks.wait(10, () -> {
				// 10t later clear the cache and reload
				// it only 'updates' once they stop clicking for 10t
				this.lines.remove(player.getUniqueId());
				this.hologram.showToPlayer(player);
			}));
			if (this.hasControls) {
				this.mechanicHologram.showToPlayer(player);
				this.statisticHologram.showToPlayer(player);
			}
		}

		public static <T> T getNextWithLoop(List<T> list, int index) {
			int nextIndex = (index + 1) % list.size();
			return list.get(nextIndex);
		}

		public static <T> T getPreviousWithLoop(List<T> list, int index) {
			int prevIndex = (index - 1 + list.size()) % list.size();
			return list.get(prevIndex);
		}

		@EventHandler
		public void onMatchEnd(MatchEndEvent event) {
			this.lines.clear();
			this.hologram.update();
		}

		@EventHandler
		public void onQuit(PlayerQuitEvent event) {
			this.lines.remove(event.getPlayer().getUniqueId());
		}

	}

	public enum DateRange {
		ALL_TIME,
		WEEKLY,
		MONTHLY,
		YEARLY;

		public LocalDateTime getDate() {
			return switch (this) {
				case ALL_TIME: yield null;
				case WEEKLY: yield LocalDateTime.now().minusWeeks(1);
				case MONTHLY: yield LocalDateTime.now().minusMonths(1);
				case YEARLY: yield LocalDateTime.now().minusYears(1);
			};
		}
	}

}
