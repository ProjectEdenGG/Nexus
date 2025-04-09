package gg.projecteden.nexus.features.minigames.lobby;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
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
		});
	}

	public static class MGMLeaderboard implements Listener {

		private static final MinigameStatsService SERVICE = new MinigameStatsService();
		private final MinigameStatistic statistic;
		private final Hologram hologram;
		private Map<UUID, List<String>> lines = new HashMap<>();

		public MGMLeaderboard(MinigameStatistic statistic) {
			this.statistic = statistic;
			this.hologram = HologramsAPI.getHolograms(Minigames.getWorld()).stream()
				.filter(hologram -> hologram.getId().equals("leaderboard_" + statistic.getId())).findFirst().orElse(null);

			if (hologram == null)
				throw new InvalidInputException("Could not find leaderboard for '" + statistic.getId() + "'");

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
			List<LeaderboardRanking> rankings = SERVICE.getLeaderboard(null, statistic, user.getLeaderboardDateRanges().getOrDefault(this.hologram.getId(), DateRange.ALL_TIME).getDate());

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

		@EventHandler
		public void onLineSpawn(HologramLineSpawnEvent event) {
			if (!event.getHologram().getLocation().getWorld().getName().equals(Minigames.getWorld().getName()))
				return;
			if (!event.getHologram().getId().equals(this.hologram.getId()))
				return;

			if (!this.lines.containsKey(event.getPlayer().getUniqueId()))
				setup(event.getPlayer());

			Object data = switch (event.getLine().getIndex()) {
				case 0: yield "&6&l" + statistic.getTitle() + " (Global)";
				case 1:
				case 14:
					yield "&f";
				case 12: yield "&7...";
				case 16: yield "&7<-- Shift Click | Click -->";
				case 15: {
					MinigameStatsUser user = SERVICE.get(event.getPlayer());
					DateRange range = user.getLeaderboardDateRanges().getOrDefault(this.hologram.getId(), DateRange.ALL_TIME);

					yield
						"&7" + StringUtils.camelCase(EnumUtils.previousWithLoop(DateRange.class, range.ordinal()).name()) +
						" &6| " +
						"&3&l" + StringUtils.camelCase(range) +
						" &6| " +
						"&7" + StringUtils.camelCase(EnumUtils.nextWithLoop(DateRange.class, range.ordinal()).name());
				}
				case 13: yield this.lines.get(event.getPlayer().getUniqueId()).get(10);
				default: yield this.lines.get(event.getPlayer().getUniqueId()).get(event.getLine().getIndex() - 2);
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
			DateRange range = user.getLeaderboardDateRanges().getOrDefault(this.hologram.getId(), DateRange.ALL_TIME);

			DateRange newRange = reverse ? EnumUtils.previousWithLoop(DateRange.class, range.ordinal()) : EnumUtils.nextWithLoop(DateRange.class, range.ordinal());
			user.getLeaderboardDateRanges().put(this.hologram.getId(), newRange);
			SERVICE.save(user);

			this.lines.remove(event.getPlayer().getUniqueId());
			this.hologram.showToPlayer(event.getPlayer());
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
