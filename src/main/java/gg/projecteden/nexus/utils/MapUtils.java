package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.models.sudoku.SudokuUser.Coordinate;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.Vector;

public class MapUtils {

	/**
	 * Converts a hit position vector on an item frame to pixel coordinates on a 128x128 map
	 * @param itemFrame The item frame containing the map
	 * @param hitPosition The hit position vector from ray tracing
	 * @return A Coordinate
	 */
	public static Coordinate getMapPixelHit(ItemFrame itemFrame, Vector hitPosition) {
		// Get the item frame's location and rotation
		Location frameLoc = itemFrame.getLocation();
		Rotation rotation = itemFrame.getRotation();

		// Convert hit position to a location
		Location hitLoc = hitPosition.toLocation(itemFrame.getWorld());

		// Get the direction the item frame is facing
		BlockFace facing = itemFrame.getFacing();

		// Calculate local coordinates on the frame (0-1 range)
		double localX, localY;

		switch (facing) {
			case NORTH:
				localX = 1 - (hitLoc.getX() - frameLoc.getBlockX());
				localY = 1 - (hitLoc.getY() - frameLoc.getBlockY());
				break;
			case SOUTH:
				localX = (hitLoc.getX() - frameLoc.getBlockX());
				localY = 1 - (hitLoc.getY() - frameLoc.getBlockY());
				break;
			case EAST:
				localX = 1 - (hitLoc.getZ() - frameLoc.getBlockZ());
				localY = 1 - (hitLoc.getY() - frameLoc.getBlockY());
				break;
			case WEST:
				localX = (hitLoc.getZ() - frameLoc.getBlockZ());
				localY = 1 - (hitLoc.getY() - frameLoc.getBlockY());
				break;
			case UP:
				localX = (hitLoc.getX() - frameLoc.getBlockX());
				localY = (hitLoc.getZ() - frameLoc.getBlockZ());
				break;
			case DOWN:
				localX = (hitLoc.getX() - frameLoc.getBlockX());
				localY = 1 - (hitLoc.getZ() - frameLoc.getBlockZ());
				break;
			default:
				localX = 0;
				localY = 0;
		}

		// Adjust for item frame rotation
		double[] adjustedCoords = adjustForRotation(localX, localY, rotation);
		localX = adjustedCoords[0];
		localY = adjustedCoords[1];

		// Clamp values to 0-1 range
		localX = Math.clamp(localX, 0, 1);
		localY = Math.clamp(localY, 0, 1);

		// Convert to pixel coordinates (0-127)
		int pixelX = (int) (localX * 128);
		int pixelY = (int) (localY * 128);

		// Ensure we're within bounds
		pixelX = Math.clamp(pixelX, 0, 127);
		pixelY = Math.clamp(pixelY, 0, 127);

		return new Coordinate(pixelX, pixelY);
	}

	/**
	 * Adjusts local coordinates based on the item frame's rotation
	 * @param x Local X coordinate (0-1)
	 * @param y Local Y coordinate (0-1)
	 * @param rotation The item frame's rotation
	 * @return Adjusted [row, col] coordinates accounting for rotation
	 */
	private static double[] adjustForRotation(double x, double y, Rotation rotation) {
		return switch (rotation) {
			case NONE -> new double[]{x, y};
			case CLOCKWISE_45, CLOCKWISE -> new double[]{y, 1 - x};
			case CLOCKWISE_135, FLIPPED -> new double[]{1 - x, 1 - y};
			case COUNTER_CLOCKWISE_45, COUNTER_CLOCKWISE -> new double[]{1 - y, x};
			default -> new double[]{x, y};
		};
	}

}
