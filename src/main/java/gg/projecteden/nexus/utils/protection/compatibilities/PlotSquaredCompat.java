package gg.projecteden.nexus.utils.protection.compatibilities;

import com.plotsquared.core.plot.Plot;
import gg.projecteden.nexus.utils.protection.ProtectionCompatibility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PlotSquaredCompat extends ProtectionCompatibility {

	public PlotSquaredCompat(JavaPlugin mainPlugin, Plugin plugin) {
		super(mainPlugin, plugin);
	}

	@Override
	public boolean canBuild(Player player, Location target) {
		Plot plot = getPlotFromLocation(target);
		return plot == null || plot.isAdded(player.getUniqueId());
	}

	@Override
	public boolean canBreak(Player player, Location target) {
		Plot plot = getPlotFromLocation(target);
		return plot == null || !plot.isDenied(player.getUniqueId());
	}

	/**
	 * @param player Player looking to interact with a block
	 * @param target Place where the player seeks to interact with a block
	 * @return true if he can interact with the block
	 */
	@Override
	public boolean canInteract(Player player, Location target) {
		Plot plot = getPlotFromLocation(target);
		return plot == null || plot.isAdded(player.getUniqueId());
	}

	/**
	 * @param player Player looking to use an item
	 * @param target Place where the player seeks to use an item at a location
	 * @return true if he can use the item at the location
	 */
	public boolean canUse(Player player, Location target) {
		Plot plot = getPlotFromLocation(target);
		return plot == null || plot.isAdded(player.getUniqueId());
	}

	private Plot getPlotFromLocation(Location location) {
		com.plotsquared.core.location.Location plotLoc = adaptBukkitLocation(location);
		if (plotLoc == null) return null;
		return Plot.getPlot(plotLoc);
	}

	private com.plotsquared.core.location.Location adaptBukkitLocation(Location location) {
		if (!location.isWorldLoaded()) return null;
		assert location.getWorld() != null;
		return com.plotsquared.core.location.Location.at(
			location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
}
