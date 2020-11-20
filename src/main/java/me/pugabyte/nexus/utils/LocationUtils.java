package me.pugabyte.nexus.utils;

import com.google.common.base.Strings;
import com.sk89q.worldedit.math.transform.AffineTransform;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.pugabyte.nexus.utils.Utils.IteratableEnum;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationUtils {
	public static Location getCenteredLocation(Location location) {
		double x = Math.floor(location.getX()) + .5;
		double y = Math.floor(location.getY());
		double z = Math.floor(location.getZ()) + .5;
		int yaw = CardinalDirection.of(location).getYaw();

		return new Location(location.getWorld(), x, y, z, yaw, 0F);
	}

	@Deprecated
	// The above method seems to be more accurate, but neither are 100% accurate
	// Doesn't do yaw/pitch
	public static Location getBlockCenter(Location location) {
		double x = Math.floor(location.getX());
		double y = Math.floor(location.getY());
		double z = Math.floor(location.getZ());

		x += (x >= 0) ? .5 : -.5;
		z += (z >= 0) ? .5 : -.5;

		return new Location(location.getWorld(), x, y, z);
	}

	@NotNull
	public static List<Location> getRandomPointInCircle(World world, int radius) {
		return getRandomPointInCircle(world, radius, 0, 0);
	}

	@NotNull
	public static List<Location> getRandomPointInCircle(World world, int radius, double xOffset, double zOffset) {
		List<Location> locationList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			double angle = Math.random() * Math.PI * 2;
			double r = Math.sqrt(Math.random());
			locationList.add(new Location(world, r * Math.cos(angle) * radius, 0, r * Math.sin(angle) * radius));
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

	public static void lookAt(Player player, Location lookAt) {
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

		public static CardinalDirection of(Player player) {
			return of(player.getLocation());
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
				location.setYaw((float) (x.startsWith("~") ? location.getYaw() + trim(yaw) : trim(yaw)));
				location.setPitch((float) (x.startsWith("~") ? location.getPitch() + trim(pitch) : trim(pitch)));
				return location;
			}
		}

		private static double trim(String string) {
			if (Strings.isNullOrEmpty(string)) return 0;
			if (Utils.isDouble(string)) return Double.parseDouble(string);
			string = StringUtils.right(string, string.length() - 1);
			if (Strings.isNullOrEmpty(string)) return 0;
			return Double.parseDouble(string);
		}
	}

}
