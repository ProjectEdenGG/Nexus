package gg.projecteden.nexus.features.statistics;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.statistics.StatisticsMenu.StatsMenus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.statistics.StatisticsUser;
import gg.projecteden.nexus.models.statistics.StatisticsUserService;
import gg.projecteden.nexus.models.statistics.StatisticsUserService.MostLeaderboardsResult;
import gg.projecteden.nexus.models.statistics.StatisticsUserService.MostLeaderboardsResult.LeaderboardStatistic;
import gg.projecteden.nexus.models.statistics.StatisticsUserService.StatisticGroup;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar.SummaryStyle;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Aliases("stats")
@NoArgsConstructor
public class StatisticsCommand extends CustomCommand implements Listener {
	private final StatisticsUserService service = new StatisticsUserService();
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

	public StatisticsCommand(CommandEvent event) {
		super(event);
	}

	static {
		StatisticGroup.updateAvailableStats();
	}

	@Path("[player]")
	@Description("Open the statistics menu")
	void check(@Arg("self") OfflinePlayer player) {
		new StatisticsMenuProvider(StatsMenus.MAIN, player).open(player(), 0);
	}

	@Async
	@Path("leaderboard <group> [stat] [--page]")
	@Description("View the leaderboard for statistic group or specific statistic")
	void leaderboard(
		StatisticGroup group,
		@Arg(tabCompleter = AvailableStatistic.class, context = 1) String stat,
		@Switch @Arg("1") int page
	) {
		if (group == StatisticGroup.CUSTOM && isNullOrEmpty(stat))
			error("Custom statistics are not summable, please choose a specific statistic");

		var values = service.getLeaderboard(group, stat);
		if (values.isEmpty())
			error("No results found");

		BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			long value = values.get(uuid);
			String string = FORMATTER.format(value);

			if (isNotNullOrEmpty(stat)) {
				if (stat.contains("time"))
					string = Timespan.ofSeconds(value / 20).format();
				if (stat.contains("one_cm"))
					string = StringUtils.distanceMetricFormat((int) value);
			}

			return json(index + " " + Nerd.of(uuid).getColoredName() + " &7- " + string);
		};

		send();
		send(PREFIX + "Leaderboard &7- &3" + camelCase(group.display()) + (isNotNullOrEmpty(stat) ? " &7- &3" + camelCase(stat) : ""));

		new Paginator<UUID>()
			.values(values.keySet())
			.formatter(formatter)
			.command("/stats leaderboard " + group.display().toLowerCase() + (isNotNullOrEmpty(stat) ? " " + stat : "") + " --page=")
			.page(page)
			.afterValues(() -> {
				if (page == 1) {
					int position = Utils.getIndexOfKey(values, uuid()) + 1;
					if (position > 10) {
						send("&7•••");
						send(formatter.apply(uuid(), "&3" + position));
					}
				}
			})
			.send();
	}

	@Async
	@Path("leaderboardsLed [player] [--page]")
	@Description("View the players leading the most leaderboards, or the leaderboards a player is leading")
	void leaderboardsLed(
		StatisticsUser user,
		@Switch @Arg("1") int page
	) {
		if (user != null) {
			MostLeaderboardsResult self = service.getMostLeaderboards().stream().filter(result -> result.getUuid().equals(user.getUuid())).findFirst().orElse(null);
			if (self == null)
				error((isSelf(user) ? "You do": user.getNickname() + " does") + " not lead any leaderboards");

			send();
			send(PREFIX + "Leaderboards Led &7- " + Nerd.of(user).getColoredName() + " &7- " + FORMATTER.format(self.getCount()));

			new Paginator<LeaderboardStatistic>()
				.values(self.getLeaderboards())
				.formatter((stat, index) -> {
					String group = StatisticGroup.valueOf(stat.getGroup().toUpperCase()).display();
					return json("&3 " + camelCase(group) + (stat.getStat() == null ? "" : " &7- &3" + camelCase(stat.getStat())))
						.command("/stats leaderboard " + group + " " + (stat.getStat() == null ? "" : stat.getStat()))
						.hover("&eClick to view leaderboard");
				})
				.command("/stats leaderboardsLed " + user.getNickname() + " --page=")
				.page(page)
				.send();
		} else {
			var values = service.getMostLeaderboards();
			if (values.isEmpty())
				error("No results found");

			BiFunction<MostLeaderboardsResult, String, JsonBuilder> formatter = (result, index) ->
				json(index + " " + Nerd.of(result.getUuid()).getColoredName() + " &7- " + FORMATTER.format(result.getCount()))
					.command("/stats leaderboardsLed " + Nerd.of(result.getUuid()).getName())
					.hover("&eClick to view leaderboards led");

			send();
			send(PREFIX + "Most Leaderboards Led &7- " + StringUtils.getCnf().format(values.stream().map(MostLeaderboardsResult::getCount).mapToInt(Integer::valueOf).sum()) + " total leaderboards");

			new Paginator<MostLeaderboardsResult>()
				.values(values)
				.formatter(formatter)
				.command("/stats leaderboardsLed --page=")
				.page(page)
				.afterValues(() -> {
					if (page == 1) {
						MostLeaderboardsResult self = values.stream().filter(result -> result.getUuid().equals(uuid())).findFirst().orElse(null);
						if (self != null) {
							int position = values.indexOf(self) + 1;
							if (position > 10) {
								send("&7•••");
								send(formatter.apply(self, "&3" + position));
							}
						}
					}
				})
				.send();
		}
	}

	@Path("update <player>")
	@Permission(Group.ADMIN)
	@Description("Copy a player's statistics to the database")
	void update(Nerd nerd) {
		service.edit(nerd, StatisticsUser::loadFromFile);
		send(PREFIX + "Updated stats for " + nerd.getNickname() + " in database");
	}

	@Async
	@SneakyThrows
	@Path("saveToDatabase")
	@Description("Save all statistics to the database. WARNING: Will override previous modifications (i.e. deleting hackers)")
	@Permission(Group.ADMIN)
	void saveToDatabase() {
		AtomicInteger count = new AtomicInteger();

		try (var filesStream = Files.list(IOUtils.getFile("server/stats").toPath())) {
			var files = filesStream.toList();
			files.stream()
				.filter(Files::isRegularFile)
				.filter(path -> path.getFileName().toString().endsWith(".json"))
				.map(path -> path.getFileName().toString().replace(".json", ""))
				.map(UUID::fromString)
				.map(service::get)
				.forEach(user -> {
					try {
						user.loadFromFile();
						service.save(user);

						String progressBar = ProgressBar.builder()
							.progress(count.incrementAndGet())
							.goal(files.size())
							.summaryStyle(SummaryStyle.NONE)
							.length(300)
							.seamless(true)
							.build();

						ActionBarUtils.sendActionBar(player(), progressBar);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
		}

		send(PREFIX + "Cached %d users".formatted(count.get()));
	}

	@Data
	public static class AvailableStatistic {}

	@TabCompleterFor(AvailableStatistic.class)
	List<String> tabCompleteAvailableStatistic(String filter, StatisticGroup context) {
		if (context == null)
			return Collections.emptyList();

		return context.getAvailableStats().stream()
			.filter(value -> value.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@TabCompleterFor(StatisticGroup.class)
	List<String> tabCompleteStatisticGroup(String filter) {
		return tabCompleteEnum(filter, StatisticGroup.class, StatisticGroup::display);
	}

	@ConverterFor(StatisticGroup.class)
	StatisticGroup convertToStatisticGroup(String value) {
		if ("misc".equalsIgnoreCase(value))
			return StatisticGroup.CUSTOM;

		return convertToEnum(value, StatisticGroup.class);
	}

	static {
		Tasks.repeatAsync(TickTime.SECOND, TickTime.SECOND.x(10), () -> {
			OnlinePlayers.getAll().forEach(player -> new StatisticsUserService().edit(player, StatisticsUser::loadFromFile));
			StatisticGroup.updateAvailableStats();
		});

		Tasks.repeatAsync(TickTime.SECOND, TickTime.MINUTE.x(5), () -> new StatisticsUserService().calculateMostLeaderboards());
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		Tasks.async(() -> {
			new StatisticsUserService().edit(event.getPlayer(), StatisticsUser::loadFromFile);
			StatisticGroup.updateAvailableStats();
		});
	}

	private static final Map<UUID, List<LocalDateTime>> ITEM_STAT_INCREASE_EVENTS = new HashMap<>();

	@EventHandler
	public void on(PlayerStatisticIncrementEvent event) {
		if (event.getStatistic() != Statistic.DROP && event.getStatistic() != Statistic.PICKUP)
			return;

		var player = event.getPlayer();
		if (Rank.of(player) != Rank.GUEST)
			return;
		if (player.getGameMode() != GameMode.CREATIVE)
			return;

		event.setCancelled(true);

		UUID uuid = player.getUniqueId();
		List<LocalDateTime> times = ITEM_STAT_INCREASE_EVENTS.computeIfAbsent(uuid, $ -> new ArrayList<>());

		int diff = event.getNewValue() - event.getPreviousValue();
		for (int i = 0; i < diff; i++)
			times.add(LocalDateTime.now());

		times.removeIf(other -> other.isBefore(LocalDateTime.now().minusMinutes(5)));
		if (times.size() < 10000)
			return;

		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(uuid, "creative-item-stat-increase-event", TickTime.MINUTE.x(5)))
			return;

		Broadcast.staff().prefix("Radar").message("Possible creative hacker: " + Nickname.of(player) + " (Too many item stat increase events in creative)").send();
	}

}
