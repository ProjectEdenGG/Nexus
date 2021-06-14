package me.pugabyte.nexus.utils;

import com.sk89q.worldedit.math.transform.AffineTransform;
import eden.utils.EnumUtils.IteratableEnum;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.particles.MathUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public class LocationUtils {
	/**
	 * Returns a copy of the provided location with X and Z coordinates centered to the block (set to .5),
	 * with pitch set to 0, and with yaw set to the closest cardinal direction.
	 */
	public static Location getCenteredLocation(Location location) {
		double x = Math.floor(location.getX()) + .5;
		double y = Math.floor(location.getY());
		double z = Math.floor(location.getZ()) + .5;
		int yaw = CardinalDirection.of(location).getYaw();

		return new Location(location.getWorld(), x, y, z, yaw, 0F);
	}

	/**
	 * Returns a copy of the provided location with X and Z coordinates centered to the block (set to .5).
	 * Leaves pitch and yaw untouched.
	 *
	 * @deprecated inaccurate and obsoleted by {@link #getCenteredLocation(Location)}
	 */
	@Deprecated
	public static Location getBlockCenter(Location location) {
		double x = Math.floor(location.getX());
		double y = Math.floor(location.getY());
		double z = Math.floor(location.getZ());

		x += (x >= 0) ? .5 : -.5;
		z += (z >= 0) ? .5 : -.5;

		return new Location(location.getWorld(), x, y, z);
	}

	/**
	 * Returns a copy of the provided location with pitch and yaw values set to 0
	 */
	public static Location clearRotation(Location location) {
		return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	/**
	 * Returns a copy of the provided location with X and Z coordinates centered to the block (set to .5) and with
	 * pitch and yaw set to 0.
	 */
	public static Location getCenteredRotationlessLocation(Location location) {
		return clearRotation(getCenteredLocation(location));
	}

	/**
	 * Returns a list of 10 random locations in a world, within the specified radius of a circle, centered at 0,0,0.
	 * <br>
	 * No guarantees are made on the validity of these locations. They may be air, in unloaded chunks, etc.
	 * <br>
	 * Locations may contain decimals and may also be duplicated. Consider mapping using {@link #getCenteredLocation(Location)}
	 * and collecting to a set if unique locations are required.
	 * @param world the world to set the locations to
	 * @param radius circle radius
	 * @return list of 10 random locations
	 */
	@NotNull
	public static List<Location> getRandomPointInCircle(World world, int radius) {
		return getRandomPointInCircle(world, radius, 0, 0, 0);
	}

	/**
	 * Returns a list of 10 random locations within the specified radius of a circle centered at the provided location.
	 * <br>
	 * No guarantees are made on the validity of these locations. They may be air, in unloaded chunks, etc.
	 * <br>
	 * Locations may contain decimals and may also be duplicated. Consider mapping using {@link #getCenteredLocation(Location)}
	 * and collecting to a set if unique locations are required.
	 * @param location the location to center the circle on
	 * @param radius circle radius
	 * @return list of 10 random locations
	 */
	@NotNull
	public static List<Location> getRandomPointInCircle(Location location, int radius) {
		return getRandomPointInCircle(location.getWorld(), radius, location.getX(), location.getY(), location.getZ());
	}

	/**
	 * Returns a list of 10 random locations in a world, within the specified radius of a circle, centered at the
	 * specified coordinates.
	 * <br>
	 * No guarantees are made on the validity of these locations. They may be air, in unloaded chunks, etc.
	 * <br>
	 * Locations may contain decimals and may also be duplicated. Consider mapping using {@link #getCenteredLocation(Location)}
	 * and collecting to a set if unique locations are required.
	 * @param world the world to set the locations to
	 * @param radius circle radius
	 * @param xOffset center X coordinate
	 * @param yOffset y coordinate
	 * @param zOffset center Z coordinate
	 * @return list of 10 random locations
	 */
	@NotNull
	public static List<Location> getRandomPointInCircle(World world, int radius, double xOffset, double yOffset, double zOffset) {
		List<Location> locationList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			double angle = Math.random() * Math.PI * 2;
			double r = Math.sqrt(Math.random());
			locationList.add(new Location(world, xOffset + (r * Math.cos(angle) * radius), yOffset, zOffset + (r * Math.sin(angle) * radius)));
		}
		return locationList;
	}

	public static Block getBlockHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		BlockIterator blockIter = new BlockIterator(projectile.getWorld(), projectile.getLocation().toVector(), projectile.getVelocity().normalize(), 0, 4);
		Block blockHit = null;

		while (blockIter.hasNext()) {
			blockHit = blockIter.next();
			if (blockHit.getType() != Material.AIR) break;
		}

		return blockHit;
	}

	/**
	 * Sets a player's pitch and yaw to look at the provided location.
	 */
	public static void lookAt(HasPlayer hasPlayer, Location lookAt) {
		Player player = hasPlayer.getPlayer();
		Vector direction = player.getEyeLocation().toVector().subtract(lookAt.add(0.5, 0.5, 0.5).toVector()).normalize();
		double x = direction.getX();
		double y = direction.getY();
		double z = direction.getZ();

		// Now change the angle
		Location changed = player.getLocation().clone();
		changed.setYaw(180 - toDegree(Math.atan2(x, z)));
		changed.setPitch(90 - toDegree(Math.acos(y)));
		player.teleport(changed);
	}

	/**
	 * Gets the facing direction in a format along the lines of "+X"
	 * @param face bukkit facing direction
	 * @return facing direction as text
	 */
	public static String getShortFacingDirection(BlockFace face) {
		Map<String, Integer> faceMap = Map.of(
				"X", face.getModX(),
				"Y", face.getModY(),
				"Z", face.getModZ()
		);
		Utils.MinMaxResult<Map.Entry<String, Integer>> max = Utils.getMax(faceMap.entrySet(), entry -> Math.abs(entry.getValue()));
		String output;
		if (max.getObject() != null) {
			if (max.getObject().getValue() > 0)
				output = "+";
			else
				output = "-";
			output += max.getObject().getKey();
		} else
			output = "N/A";
		return output;
	}

	/**
	 * Gets a player's facing direction in a format along the lines of "+X"
	 * @param player player to get
	 * @return facing direction as text
	 */
	public static String getShortFacingDirection(HasPlayer player) {
		return getShortFacingDirection(player.getPlayer().getFacing());
	}

	@NotNull
	public static Location parse(@NotNull String text) throws IllegalArgumentException {
		Validate.notNull(text, "input doesn't exist");
		String[] split = StringUtils.stripColor(text).split(" ");
		Validate.isTrue(split.length == 4 || split.length == 6, "text contains incorrect number of arguments (expected: 4 or 6, got: " + split.length + ") [" + text + "]");
		World world = Bukkit.getWorld(split[0]);
		if (world == null)
			throw new IllegalArgumentException("world \"" + split[0] +"\" doesn't exist");
		Location location = new Location(world,
				Double.parseDouble(split[1]),
				Double.parseDouble(split[2]),
				Double.parseDouble(split[3]));
		if (split.length == 4)
			return location;
		location.setYaw(Float.parseFloat(split[4]));
		location.setPitch(Float.parseFloat(split[5]));
		return location;
	}

	@Nullable
	public static Location parseOrNull(String text) {
		try {
			return parse(text);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Tests if two locations are equal to each other while avoiding false negatives from floating point issues
	 */
	public static boolean locationsEqual(@Nullable Location location1, @Nullable Location location2) {
		if (location1 == null || location2 == null) return false;
		if (!location1.getWorld().equals(location2.getWorld())) return false;
		if (Math.abs(location1.getX() - location2.getX()) > MathUtils.FLOAT_ROUNDING_ERROR) return false;
		if (Math.abs(location1.getY() - location2.getY()) > MathUtils.FLOAT_ROUNDING_ERROR) return false;
		if (Math.abs(location1.getZ() - location2.getZ()) > MathUtils.FLOAT_ROUNDING_ERROR) return false;
		if (Math.abs(location1.getYaw() - location2.getYaw()) > MathUtils.FLOAT_ROUNDING_ERROR) return false;
		return !(Math.abs(location1.getPitch() - location2.getPitch()) > MathUtils.FLOAT_ROUNDING_ERROR);
	}

	private static float toDegree(double angle) {
		return (float) Math.toDegrees(angle);
	}

	public enum EgocentricDirection {
		LEFT,
		RIGHT
	}

	public enum CardinalDirection implements IteratableEnum {
		NORTH(180),
		EAST(270),
		SOUTH(0),
		WEST(90);

		@Getter
		private final int yaw;

		CardinalDirection(int yaw) {
			this.yaw = yaw;
		}

		public static CardinalDirection of(BlockFace blockFace) {
			return CardinalDirection.valueOf(blockFace.name());
		}

		public static CardinalDirection of(HasPlayer player) {
			return of(player.getPlayer().getLocation());
		}

		public static CardinalDirection of(Location location) {
			float yaw = location.getYaw();
			if (yaw < 0) yaw += 360;

			CardinalDirection direction = SOUTH;
			if (yaw < 315) direction = EAST;
			if (yaw < 225) direction = NORTH;
			if (yaw < 135) direction = WEST;
			if (yaw < 45) direction = SOUTH;
			return direction;
		}

		public static CardinalDirection random() {
			return RandomUtils.randomElement(values());
		}

		// Clockwise
		public CardinalDirection turnRight() {
			return nextWithLoop();
		}

		// Counter-clockwise
		public CardinalDirection turnLeft() {
			return previousWithLoop();
		}

		public BlockFace toBlockFace() {
			return BlockFace.valueOf(name());
		}

		public Vector toVector() {
			return toBlockFace().getDirection();
		}

		public static BlockFace[] blockFaces() {
			return Arrays.stream(values()).map(CardinalDirection::toBlockFace).toArray(BlockFace[]::new);
		}

		public int getRotation() {
			return ordinal() * -90;
		}

		public AffineTransform getRotationTransform() {
			return new AffineTransform().rotateY(getRotation());
		}

		public static boolean isCardinal(BlockFace face) {
			try {
				return CardinalDirection.of(face) != null;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

	public enum Axis {
		X,
		Y,
		Z;

		public static Axis getAxis(Location location1, Location location2) {
			if (Math.floor(location1.getX()) == Math.floor(location2.getX()) && Math.floor(location1.getZ()) == Math.floor(location2.getZ()))
				return Y;
			if (Math.floor(location1.getX()) == Math.floor(location2.getX()))
				return X;
			if (Math.floor(location1.getZ()) == Math.floor(location2.getZ()))
				return Z;

			return null;
		}
	}

	public static class RelativeLocation {

		public static Modify modify(Location location) {
			return new Modify(location);
		}

		@Data
		@Accessors(fluent = true)
		public static class Modify {
			@NonNull
			private Location location;
			private String x;
			private String y;
			private String z;
			private String yaw;
			private String pitch;

			public Modify(@NonNull Location location) {
				this.location = location;
			}

			public Location update() {
				location.setX((x.startsWith("~") ? location.getX() + trim(x) : trim(x)));
				location.setY((y.startsWith("~") ? location.getY() + trim(y) : trim(y)));
				location.setZ((z.startsWith("~") ? location.getZ() + trim(z) : trim(z)));
				if (!isNullOrEmpty(yaw))
					location.setYaw((float) (yaw.startsWith("~") ? location.getYaw() + trim(yaw) : trim(yaw)));
				if (!isNullOrEmpty(pitch))
					location.setPitch((float) (pitch.startsWith("~") ? location.getPitch() + trim(pitch) : trim(pitch)));
				return location;
			}
		}

		private static double trim(String string) {
			if (isNullOrEmpty(string)) return 0;
			if (Utils.isDouble(string)) return Double.parseDouble(string);
			string = StringUtils.right(string, string.length() - 1);
			if (isNullOrEmpty(string)) return 0;
			return Double.parseDouble(string);
		}
	}

	public static boolean blockLocationsEqual(@Nullable Location location1, @Nullable Location location2) {
		if (location1 == null || location2 == null) return false;
		return location1.getWorld().equals(location2.getWorld()) &&
				location1.getBlockX() == location2.getBlockX() &&
				location1.getBlockY() == location2.getBlockY() &&
				location1.getBlockZ() == location2.getBlockZ();
	}

	public static boolean isFuzzyEqual(@Nullable Location location1, @Nullable Location location2) {
		if (locationsEqual(location1, location2))
			return true;

		return blockLocationsEqual(location1, location2);
	}

}
