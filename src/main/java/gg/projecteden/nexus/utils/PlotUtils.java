package gg.projecteden.nexus.utils;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class PlotUtils {

	public static com.plotsquared.core.location.Location adapt(Location location) {
		var bv3 = new WorldEditUtils(location).toBlockVector3(location);
		return com.plotsquared.core.location.Location.at(location.getWorld().getName(), bv3, 0, 0);
	}

	public static PlotArea getPlotArea(Location location) {
		var area = getNullablePlotArea(location);
		if (area == null)
			throw new InvalidInputException("No plot area found at " + StringUtils.xyzw(location));
		return area;
	}

	public static @Nullable PlotArea getNullablePlotArea(Location location) {
		return PlotSquared.get().getPlotAreaManager().getPlotArea(adapt(location));
	}

	public static Plot getPlot(Location location) {
		return getPlotArea(location).getPlot(adapt(location));
	}

	public static @NotNull List<Chunk> getChunks(Plot plot) {
		World world = requireNonNull(Bukkit.getWorld(requireNonNull(plot.getWorldName())));

		return plot.getLargestRegion().getChunks().stream()
			.map(chunk -> world.getChunkAt(chunk.x(), chunk.z()))
			.toList();
	}

}
