package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;

public class BorderUtils {

	public static boolean isOutsideBorder(Player player) {
		Location loc = player.getLocation();
		WorldBorder border = player.getWorld().getWorldBorder();
		double size = border.getSize() / 2;
		Location center = border.getCenter();
		double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
		return ((x > size || -x > size) || (z > size || -z > size));
	}

	public static void moveInsideBorder(Player player) {
		moveInsideBorder(player, 150);
	}

	public static void moveInsideBorder(Player player, int buffer) {
		final World world = player.getWorld();
		final Location location = player.getLocation();
		final Minigamer minigamer = Minigamer.of(player);

		final WorldBorder worldBorder = world.getWorldBorder();
		final Location center = worldBorder.getCenter();
		final double diameter = worldBorder.getSize();
		final double width = diameter - (buffer * 2);
		final double height = diameter - (buffer * 2);
		final Rectangle2D.Double border = new Rectangle2D.Double(-(width / 2), -(height / 2), width, height);

//		Dev.GRIFFIN.send("World border center: " + StringUtils.getFlooredCoordinateString(center));
//		Dev.GRIFFIN.send("World border size: %f (%f x %f) (%f, %f -> %f, %f)", diameter, width, height,
//			border.getMinX(), border.getMinY(), border.getMaxX(), border.getMaxY());

		final Point2D point = MathUtils.getIntersectPoint(new Double(center.getX(), center.getZ(), location.getX(), location.getZ()), border);

		if (point == null)
			throw new InvalidInputException("Could not move inside border, intersection point is null");

		final Location newLocation = new Location(world, point.getX(), 0, point.getY());
		newLocation.getWorld().getChunkAtAsync(newLocation, true).thenAccept(chunk -> {
			Block highestBlock = world.getHighestBlockAt(newLocation);

			if (!highestBlock.getType().isSolid()) {
				moveInsideBorder(player, buffer + 1);
				return;
			}

			final Location teleportTo = LocationUtils.getCenteredLocation(highestBlock.getLocation().add(0, 1, 0));
			if (minigamer.isPlaying())
				minigamer.teleportAsync(teleportTo);
			else
				player.teleportAsync(teleportTo);
		});
	}

	@Getter
	@EqualsAndHashCode
	@AllArgsConstructor
	public static final class WorldBorderWrapper {
		private final @NotNull Duration delay;
		private final @NotNull Duration shrink;
		private final @NotNull Duration warning;
		private final int diameter;

		public String getDiameterString() {
			return diameter + "x" + diameter;
		}
	}

}
