package gg.projecteden.nexus.features.modeltrain;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rail.Shape;
import org.bukkit.util.Vector;

public class ModelTrainUtils {

	protected static float yawFromVector(Vector v) {
		return (float) Math.toDegrees(Math.atan2(-v.getX(), v.getZ()));
	}

	protected static float pitchFromVector(Vector v) {
		return (float) -Math.toDegrees(Math.asin(v.getY()));
	}

	protected static float shortestAngleDelta(float from, float to) {
		double rad = Math.atan2(
			Math.sin(Math.toRadians(to - from)),
			Math.cos(Math.toRadians(to - from))
		);
		return (float) Math.toDegrees(rad);
	}

	protected static Vector snapToCardinal(Vector v) {
		// Slopes
		if (Math.abs(v.getY()) > 0.1) {
			return new Vector(
				Math.signum(v.getX()),
				Math.signum(v.getY()),
				Math.signum(v.getZ())
			);
		}

		// Flat
		return Math.abs(v.getX()) > Math.abs(v.getZ()) ?
			new Vector(Math.signum(v.getX()), 0, 0)
			: new Vector(0, 0, Math.signum(v.getZ()));
	}

	protected static RailBlockFace getBlockFace(Vector dir) {
		double x = dir.getX();
		double z = dir.getZ();

		if (Math.abs(x) > Math.abs(z))
			return x > 0 ? RailBlockFace.EAST : RailBlockFace.WEST;

		return z > 0 ? RailBlockFace.SOUTH : RailBlockFace.NORTH;
	}

	protected static double getRailSurfaceY(Vector pos, double yOffset, Block railBlock) {
		double baseY = railBlock.getY() + 0.1 + yOffset;

		if (!(railBlock.getBlockData() instanceof Rail rail))
			return baseY;

		return switch (rail.getShape()) {
			case ASCENDING_NORTH -> baseY + clamp01(1.0 - (pos.getZ() - railBlock.getZ()));
			case ASCENDING_SOUTH -> baseY + clamp01(pos.getZ() - railBlock.getZ());
			case ASCENDING_EAST -> baseY + clamp01(pos.getX() - railBlock.getX());
			case ASCENDING_WEST -> baseY + clamp01(1.0 - (pos.getX() - railBlock.getX()));
			default -> baseY;
		};
	}

	private static double clamp01(double v) {
		return Math.max(0, Math.min(1, v));
	}

	protected static Block getRailBlockAtPosition(Vector pos, double yOffset, World world) {
		Vector posOffset = pos.setY(pos.getY() + yOffset);
		Block base = posOffset.toLocation(world).getBlock();

		if (base.getBlockData() instanceof Rail) return base;

		Block below = base.getRelative(0, -1, 0);
		if (below.getBlockData() instanceof Rail) return below;

		Block above = base.getRelative(0, 1, 0);
		if (above.getBlockData() instanceof Rail) return above;

		return null; // derailed
	}

	protected static Vector readRailDirection(Block block, Vector direction) {
		if (!(block.getBlockData() instanceof Rail rail))
			return direction;

		return RailDirection
			.fromShape(rail.getShape())
			.getDirection(getBlockFace(direction), direction);
	}

	@Getter
	@AllArgsConstructor
	protected enum RailBlockFace {
		NORTH(new Vector(0, 0, -1)),
		SOUTH(new Vector(0, 0, 1)),
		EAST(new Vector(1, 0, 0)),
		WEST(new Vector(-1, 0, 0));

		private final Vector direction;
	}

	@Getter
	@AllArgsConstructor
	protected enum RailDirection {
		// ----------- STRAIGHT RAILS -----------
		NORTH_SOUTH(Shape.NORTH_SOUTH) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				return (approach == RailBlockFace.NORTH)
					? RailBlockFace.NORTH.getDirection()
					: RailBlockFace.SOUTH.getDirection();
			}
		},

		EAST_WEST(Shape.EAST_WEST) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				return (approach == RailBlockFace.EAST)
					? RailBlockFace.EAST.getDirection()
					: RailBlockFace.WEST.getDirection();
			}
		},

		// ----------- SLOPES -----------
		ASCENDING_NORTH(Shape.ASCENDING_NORTH) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				double z = Math.signum(direction.getZ());
				return new Vector(0, z < 0 ? 1 : -1, z);
			}
		},

		ASCENDING_SOUTH(Shape.ASCENDING_SOUTH) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				double z = Math.signum(direction.getZ());
				return new Vector(0, z > 0 ? 1 : -1, z);
			}
		},

		ASCENDING_EAST(Shape.ASCENDING_EAST) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				double x = Math.signum(direction.getX());
				return new Vector(x, x > 0 ? 1 : -1, 0);
			}
		},

		ASCENDING_WEST(Shape.ASCENDING_WEST) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				double x = Math.signum(direction.getX());
				return new Vector(x, x < 0 ? 1 : -1, 0);
			}
		},

		// ----------- CURVES -----------
		SOUTH_EAST(Shape.SOUTH_EAST) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				return switch (approach) {
					case NORTH, EAST -> RailBlockFace.EAST.getDirection();
					case WEST, SOUTH -> RailBlockFace.SOUTH.getDirection();
				};
			}
		},

		SOUTH_WEST(Shape.SOUTH_WEST) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				return switch (approach) {
					case NORTH, WEST -> RailBlockFace.WEST.getDirection(); // go WEST
					case EAST, SOUTH -> RailBlockFace.SOUTH.getDirection();  // go SOUTH
				};
			}
		},

		NORTH_EAST(Shape.NORTH_EAST) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				return switch (approach) {
					case SOUTH, EAST -> RailBlockFace.EAST.getDirection();
					case WEST, NORTH -> RailBlockFace.NORTH.getDirection();
				};
			}
		},

		NORTH_WEST(Shape.NORTH_WEST) {
			@Override
			public Vector getDirection(RailBlockFace approach, Vector direction) {
				return switch (approach) {
					case SOUTH, WEST -> RailBlockFace.WEST.getDirection();
					case EAST, NORTH -> RailBlockFace.NORTH.getDirection();
				};
			}
		};

		public abstract Vector getDirection(RailBlockFace approach, Vector direction);

		private final Rail.Shape shape;

		public static RailDirection fromShape(Rail.Shape shape) {
			for (RailDirection direction : values()) {
				if (direction.getShape() == shape)
					return direction;
			}

			throw new InvalidInputException("Invalid shape: " + shape);
		}
	}
}
