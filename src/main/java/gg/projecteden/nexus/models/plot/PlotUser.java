package gg.projecteden.nexus.models.plot;

import com.plotsquared.core.plot.Plot;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity(value = "plot_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class PlotUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, Map<String, PlotInfo>> plots = new HashMap<>();

	@Data
	@NoArgsConstructor
	@Entity(noClassnameStored = true)
	public static class PlotInfo {
		private String id;
		private Map<Material, Integer> distr = new HashMap<>();
		private boolean forceKeep;

		public PlotInfo(Plot plot) {
			this(plot.getId().toString());
		}

		public PlotInfo(String id) {
			this.id = id;
		}
	}

	public Rating getRating(Plot plot) {
		var world = this.plots.get(plot.getWorldName());

		if (world == null)
			throw new InvalidInputException("Plot " + plot.getId() + " has no block distribution data");

		var plotInfo = world.get(plot.getId().toString());

		if (plotInfo == null)
			throw new InvalidInputException("Plot " + plot.getId() + " has no block distribution data");

		var distr = plotInfo.getDistr();
		var sum = distr.values().stream().mapToInt(Integer::valueOf).sum();
		double rating = Math.log10(sum) * distr.size();
		return new Rating(sum, distr.size(), Double.isNaN(rating) ? 0 : rating);
	}

	public PlotInfo getPlotInfo(Plot plot) {
		return plots
			.computeIfAbsent(plot.getWorldName(), $ -> new HashMap<>())
			.computeIfAbsent(plot.getId().toString(), $ -> new PlotInfo(plot));
	}

	public RatingResult getRatingResult(Plot plot) {
		RatingResult result;
		var rating = getRating(plot);

		if (getPlotInfo(plot).isForceKeep())
			result = RatingResult.KEEP_FORCE;
		else if (rating.getRating() <= 20)
			result = RatingResult.DELETE;
		else if (rating.getRating() <= 50)
			result = RatingResult.REVIEW;
		else
			result = RatingResult.KEEP_THRESHOLD;

		if (!result.isKeep()) {
			LocalDateTime lastQuit = Nerd.of(this).getLastQuit(null);
			if (lastQuit != null && lastQuit.isAfter(LocalDateTime.now().minusYears(1)))
				result = RatingResult.KEEP_LASTQUIT;

			var hours = new HoursService().get(this).getTotal();
			if (hours >= TickTime.DAY.get() / 20)
				result = RatingResult.KEEP_HOURS;
		}

		return result;
	}

	private static List<Material> IGNORE_MATERIALS = List.of(
		Material.GRASS_BLOCK,
		Material.STONE,
		Material.AIR,
		Material.VOID_AIR,
		Material.CAVE_AIR,
		Material.BEDROCK
	);

	public void calculateDistr(Plot plot) {
		Map<Material, Integer> distr = new HashMap<>();

		var worldedit = new WorldEditUtils(Objects.requireNonNull(plot.getWorldName()));
		var blocks = worldedit.getBlocks(plot.getLargestRegion());
		for (Block block : blocks)
			if (!IGNORE_MATERIALS.contains(block.getType()))
				distr.put(block.getType(), distr.getOrDefault(block.getType(), 0) + 1);

		for (Material material : new HashSet<>(distr.keySet()))
			if (distr.get(material) <= 10)
				distr.remove(material);

		getPlotInfo(plot).setDistr(distr);
	}

	@Data
	@AllArgsConstructor
	public static class Rating {
		final int sum;
		final int distr;
		final double rating;
	}

	@AllArgsConstructor
	public enum RatingResult {
		DELETE(ChatColor.RED),
		REVIEW(ChatColor.GOLD),
		KEEP_FORCE(ChatColor.GREEN),
		KEEP_THRESHOLD(ChatColor.GREEN),
		KEEP_LASTQUIT(ChatColor.GREEN),
		KEEP_HOURS(ChatColor.GREEN),
		;

		private final ChatColor color;

		public String camelCase() {
			return color + StringUtils.camelCase(name())
				.replace("Keep", "Keep -")
				.replace("Lastquit", "Last Quit");
		}

		public boolean isKeep() {
			return name().startsWith("KEEP");
		}
	}

}
