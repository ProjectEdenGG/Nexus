package gg.projecteden.nexus.features.statistics;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.statistics.StatisticsMenu.StatsMenus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.statistics.StatisticsUser;
import gg.projecteden.nexus.models.statistics.StatisticsUserService;
import gg.projecteden.nexus.models.statistics.StatisticsUserService.StatisticGroup;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar.SummaryStyle;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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
	@SneakyThrows
	@Path("saveToDatabase")
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

	@Async
	@Path("leaderboard <group> [stat] [--page]")
	void leaderboard(StatisticGroup group, @Arg(tabCompleter = AvailableStatistic.class, context = 1) String stat, @Switch @Arg("1") int page) {
		if (group == StatisticGroup.CUSTOM && isNullOrEmpty(stat))
			error("Custom statistics are not summable, please choose a specific statistic");

		LinkedHashMap<UUID, Long> values = service.getLeaderboard(group, stat);
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
		send(PREFIX + "Leaderboard &7- &3" + camelCase(group) + (isNotNullOrEmpty(stat) ? " &7- &3" + camelCase(stat) : ""));

		new Paginator<UUID>()
			.values(values.keySet())
			.formatter(formatter)
			.command("/stats leaderboard " + group.name().toLowerCase() + (isNotNullOrEmpty(stat) ? " " + stat : "") + " --page=")
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

	@EventHandler
	public void on(PlayerStatisticIncrementEvent event) {
		Tasks.async(() -> {
			Player player = event.getPlayer();
			if (new CooldownService().check(player.getUniqueId(), "statistics-cache", TickTime.SECOND.x(10))) {
				new StatisticsUserService().edit(player, StatisticsUser::loadFromFile);
				StatisticGroup.updateAvailableStats();
			}
		});
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		Tasks.async(() -> {
			new StatisticsUserService().edit(event.getPlayer(), StatisticsUser::loadFromFile);
			StatisticGroup.updateAvailableStats();
		});
	}

}
