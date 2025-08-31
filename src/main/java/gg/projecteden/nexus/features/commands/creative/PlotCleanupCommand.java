package gg.projecteden.nexus.features.commands.creative;

import com.plotsquared.core.plot.Plot;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.plot.PlotUser.RatingResult;
import gg.projecteden.nexus.models.plot.PlotUserService;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar.SummaryStyle;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import lombok.SneakyThrows;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static gg.projecteden.api.common.utils.StringUtils.getDf;
import static gg.projecteden.nexus.utils.PlotUtils.getChunks;
import static gg.projecteden.nexus.utils.PlotUtils.getPlot;
import static gg.projecteden.nexus.utils.PlotUtils.getPlotArea;

@Permission(Group.ADMIN)
public class PlotCleanupCommand extends CustomCommand {

	public PlotCleanupCommand(CommandEvent event) {
		super(event);
	}

	@Path("info [--showDistr]")
	void info(@Switch boolean showDistr) {
		var plot = getPlot(location());
		if (plot == null)
			error("No plot found");

		var uuid = plot.getOwner();
		if (uuid == null)
			error("No owner found");

		info(plot);
	}

	private void info(Plot plot) {
		line();
		var uuid = plot.getOwner();
		var owner = Nerd.of(uuid);
		var seen = Timespan.of(owner.getLastQuit(player())).format();
		var seenDate = TimeUtils.longDateTimeFormat(owner.getLastQuit());
		var playtime = new HoursService().get(owner).getTotal();
		var hours = TimespanBuilder.ofSeconds(playtime).noneDisplay(true).format();
		var user = new PlotUserService().get(owner);
		var rating = user.getRating(plot);

		send("&3Plot ID: &e" + plot.getId());
		send("&3Owner: " + owner.getColoredName());
		send("&3Seen: &e%s ago &3(%s)".formatted(seen, seenDate));
		send("&3Hours: &e%s".formatted(hours));
		send("&3Total Blocks: &e" + rating.getSum());
		send("&3Unique Blocks: &e" + rating.getDistr());
		send("&3Rating: &e" + getDf().format(rating.getRating()));
		send("&3Result: &e" + user.getRatingResult(plot).camelCase());
	}

	@Path("next")
	void next() {
		getSortedPlots().stream()
			.filter(plot -> {
				var user = new PlotUserService().get(plot.getOwner());
				return user.getRatingResult(plot) == RatingResult.DELETE;
			})
			.findFirst()
			.ifPresentOrElse(
				plot -> runCommand("plot visit " + plot.getId()),
				() -> error("No plots ready for deletion")
			);
	}

	@Path("forceKeep [state]")
	void forceKeep(Boolean state) {
		var plot = getPlot(location());
		if (plot == null)
			error("No plot found");

		var uuid = plot.getOwner();
		if (uuid == null)
			error("No owner found");

		var service = new PlotUserService();
		var user = service.get(plot.getOwner());
		var plotInfo = user.getPlotInfo(plot);

		plotInfo.setForceKeep(state == null ? !plotInfo.isForceKeep() : state);
		send(PREFIX + (plotInfo.isForceKeep() ? "&aEnabled" : "&cDisabling") + " &3force keep for for &e" + plot.getId() + " " + Nerd.of(uuid).getColoredName());
	}

	@Async
	@Path("autodelete")
	void autodelete() {
		var plot = getPlot(location());
		if (plot == null)
			error("No plot found");

		var uuid = plot.getOwner();
		if (uuid == null)
			error("No owner found");

		var service = new PlotUserService();
		var user = service.get(uuid);

		user.calculateDistr(plot);

		var rating = user.getRatingResult(plot);
		if (rating != RatingResult.DELETE)
			error("Plot is not eligible for deletion. Current rating: " + rating.camelCase());

		info(false);

		runCommand("plot delete " + plot.getId());
		user.getPlots().get(world().getName()).remove(plot.getId().toString());
		service.save(user);
	}

	@Path("list [page]")
	void list(@Arg("1") int page) {
		new Paginator<Plot>()
			.values(getSortedPlots())
			.formatter((plot, index) -> {
				var user = new PlotUserService().get(plot.getOwner());
				var rating = getDf().format(user.getRating(plot).getRating());
				var ratingResult = user.getRatingResult(plot).camelCase();
				return json(index + " &e" + Nerd.of(user).getColoredName() + " &7- " + rating + " (" + ratingResult + "&7)")
					.command("plot visit " + plot.getId())
					.hover("&eClick to teleport");
				}
			)
			.command("/plotcleanup list")
			.page(page)
			.send();
	}

	private @NotNull ArrayList<Plot> getSortedPlots() {
		var plots = new ArrayList<>(getPlotArea(location()).getBasePlots());
		plots.sort(Comparator.comparingDouble(plot -> {
			var user = new PlotUserService().get(plot.getOwner());
			return user.getRating(plot).getRating();
		}));
		return plots;
	}

	@Async
	@Path("stats")
	void stats() {
		var plots = getPlotArea(location()).getBasePlots();
		Map<RatingResult, Integer> counts = new HashMap<>();

		var service = new PlotUserService();

		for (Plot plot : plots) {
			var ratingResult = service.get(plot.getOwner()).getRatingResult(plot);
			counts.put(ratingResult, counts.getOrDefault(ratingResult, 0) + 1);
		}

		line();
		send(PREFIX + "Stats");
		for (RatingResult ratingResult : RatingResult.values())
			send("&3" + ratingResult.camelCase() + ": &e" + counts.getOrDefault(ratingResult, 0) + " (" + counts.getOrDefault(ratingResult, 0) * 100 / plots.size() + "%)");
	}

	@Async
	@Path("recalculate")
	void recalculate() {
		var plot = getPlot(location());
		if (plot == null)
			error("No plot found");

		var uuid = plot.getOwner();
		if (uuid == null)
			error("No owner found");

		var start = System.currentTimeMillis();
		new PlotUserService().edit(uuid, user -> user.calculateDistr(plot));
		send(PREFIX + "Recalculated plot " + plot.getId() + " for " + Nerd.of(uuid).getColoredName() + " &3in &e" + (System.currentTimeMillis() - start) + "ms");
		info(false);
	}

	@Async
	@SneakyThrows
	@Path("recalculateAll [delay]")
	void recalculateAll(@Arg("500") int delay) {
		var plots = getPlotArea(location()).getBasePlots();

		send(PREFIX + "&3Count: &e" + plots.size());

		int index = 0;
		for (Plot plot : plots) {
			++index;
			var uuid = plot.getOwner();
			if (uuid == null)
				continue;

			var service = new PlotUserService();
			var user = service.get(uuid);

			var chunks = getChunks(plot);
			var future = new CompletableFuture<Boolean>();
			Tasks.sync(() -> {
				try {
					for (Chunk chunk : chunks) {
						chunk.setForceLoaded(true);
						if (!chunk.load())
							throw new RuntimeException("Failed to load chunk " + chunk.getX() + ", " + chunk.getZ());
					}
					future.complete(true);
				} catch (Exception ex) {
					chunks.forEach(chunk -> chunk.setForceLoaded(false));
					Nexus.log(ex.getMessage(), ex);
					future.complete(false);
				}
			});

			try {
				var success = future.get();

				if (!success)
					throw new RuntimeException("Failed to load chunks for plot " + plot.getId());

				for (var chunk : chunks)
					if (!chunk.isLoaded())
						throw new RuntimeException("Chunk " + chunk.getX() + ", " + chunk.getZ() + " not loaded");

				user.calculateDistr(plot);
				service.save(user);
			} catch (Exception ex) {
				chunks.forEach(chunk -> chunk.setForceLoaded(false));
				send(PREFIX + "Failed to calculate plot " + plot.getId() + " for " + Nerd.of(uuid).getColoredName() + ": " + ex.getMessage());
				throw ex;
			}

			chunks.forEach(chunk -> chunk.setForceLoaded(false));

			info(plot);

			String progressBar = ProgressBar.builder()
				.progress(index)
				.goal(plots.size())
				.summaryStyle(SummaryStyle.NONE)
				.length(300)
				.seamless(true)
				.build();

			new TitleBuilder().subtitle(progressBar).fadeIn(0).players(player()).send();

			Thread.sleep(delay);
		}

		stats();
	}

	@Path("validateChunk")
	void validateChunk() {
		var plot = getPlot(getTargetBlockRequired().getLocation());
		if (plot == null)
			error("No plot found");

		var contains = getChunks(plot).contains(location().getChunk());
		send(PREFIX + "Contains chunk: " + (contains ? "&aYes" : "&cNo"));
	}

	static {
		Tasks.repeatAsync(TickTime.MINUTE, TickTime.MINUTE, () -> {
			OnlinePlayers.where().world("creative").forEach(player -> {
				var plot = getPlot(player.getLocation());
				if (plot == null)
					return;

				UUID owner = plot.getOwner();
				if (owner == null)
					return;

				var chunks = getChunks(plot);
				if (chunks.stream().anyMatch(chunk -> !chunk.isLoaded()))
					return;

				new PlotUserService().edit(owner, user -> {
					user.calculateDistr(plot);
					double rating = user.getRating(plot).getRating();
					Debug.log("[PlotCleanup] Recalculated plot " + user.getNickname() + " " + plot.getId() + ". Rating: " + getDf().format(rating));
				});
			});
		});
	}

}
