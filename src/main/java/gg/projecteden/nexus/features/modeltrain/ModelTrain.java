package gg.projecteden.nexus.features.modeltrain;

import gg.projecteden.nexus.utils.LocationUtils.CardinalDirection;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ModelTrain {
	private final Location startLocation;
	private final World world;
	private int task = -1;
	private boolean active = false;
	private double speed = 0.1;
	private Vector direction;
	//
	private List<ArmorStand> cars = new ArrayList<>();
	private ArmorStand engineStand = null;
	private Vector enginePos = null;
	private float previousYaw = 0f;
	private float previousPitch = 0f;
	private Block previousBlock = null;

	public ModelTrain(Location startLocation) {
		this.startLocation = startLocation;
		this.world = startLocation.getWorld();
		start();
	}

	public void stop() {
		active = false;
		Tasks.cancel(task);
		engineStand.remove();
		for (ArmorStand car : cars) {
			car.remove();
		}
	}

	public void start() {
		engineStand = world.spawn(startLocation, ArmorStand.class, stand -> {
			stand.setHeadPose(EulerAngle.ZERO);
			stand.setGravity(false);
			stand.setInvulnerable(true);
			stand.setHelmet(new ItemStack(Material.PLAYER_HEAD));
		});

		// Start pointing in some direction, then snap to cardinal
		direction = snapToCardinal(startLocation.getDirection());

		// Start position as a vector
		enginePos = startLocation.toVector();

		active = true;
		task = Tasks.repeat(0, 1, () -> {
			if (!active)
				return;

			// 1. Find the rail block we're currently on
			Block currentBlock = getRailBlockAtPosition(enginePos);

			if (currentBlock == null)
				return; // derailed

			// 2. If we entered a NEW rail block, update direction from shape + approach
			if (!currentBlock.equals(previousBlock)) {

				Vector newDirection = readRailDirection(currentBlock);
				direction = snapToCardinal(newDirection);

				updateHeadPose(newDirection);

				// Snap to block center on X/Z when we first enter it
				enginePos.setX(currentBlock.getX() + 0.5);
				enginePos.setZ(currentBlock.getZ() + 0.5);
				enginePos.setY(currentBlock.getY() + 0.1);

				// LAST
				previousBlock = currentBlock;
			}

			// 3. Move forward along current direction
			moveForward(direction, true);
		});
	}

	private void moveForward(Vector direction, boolean updateHeadPose) {

		// Move forward in X/Y/Z
		enginePos.add(direction.clone().multiply(speed));

		// Attach to the actual rail surface EVERY TICK
		Block railBlock = getRailBlockAtPosition(enginePos);
		if (railBlock != null) {
			double surfaceY = getRailSurfaceY(enginePos, railBlock, direction);
			enginePos.setY(surfaceY);
		}

		engineStand.teleport(enginePos.toLocation(world));

		if (updateHeadPose) {
			engineStand.setHeadPose(new EulerAngle(
				Math.toRadians(previousPitch),
				Math.toRadians(previousYaw),
				0
			));
		}
	}

	private void updateHeadPose(Vector direction) {
		float targetYaw = yawFromVector(direction);
		float targetPitch = pitchFromVector(direction);

		previousYaw = lerpAngle(previousYaw, targetYaw, 0.5f);
		previousPitch = lerp(previousPitch, targetPitch, 0.5f);

		engineStand.setHeadPose(new EulerAngle(
			Math.toRadians(previousPitch),
			Math.toRadians(previousYaw),
			0
		));
	}

	private double getRailSurfaceY(Vector pos, Block railBlock, Vector motion) {
		double baseY = railBlock.getY() + 0.1; // visual rail surface height

		if (!(railBlock.getBlockData() instanceof Rail rail))
			return baseY;

		Rail.Shape shape = rail.getShape();

		// Flat rails → constant height
		if (!shape.name().startsWith("ASCENDING"))
			return baseY;

		// SLOPES → interpolate Y based on local block progress
		double frac;

		if (shape == Rail.Shape.ASCENDING_NORTH) {
			frac = 1.0 - (pos.getZ() - railBlock.getZ());
		} else if (shape == Rail.Shape.ASCENDING_SOUTH) {
			frac = (pos.getZ() - railBlock.getZ());
		} else if (shape == Rail.Shape.ASCENDING_EAST) {
			frac = (pos.getX() - railBlock.getX());
		} else if (shape == Rail.Shape.ASCENDING_WEST) {
			frac = 1.0 - (pos.getX() - railBlock.getX());
		} else {
			return baseY;
		}

		frac = Math.max(0, Math.min(1, frac)); // clamp

		return baseY + frac;
	}

	private Block getRailBlockAtPosition(Vector pos) {
		Block base = pos.toLocation(world).getBlock();

		if (base.getBlockData() instanceof Rail)
			return base;

		Block below = base.getRelative(0, -1, 0);
		if (below.getBlockData() instanceof Rail)
			return below;

		Block above = base.getRelative(0, 1, 0);
		if (above.getBlockData() instanceof Rail)
			return above;

		return null; // actually derailed
	}

	private Vector readRailDirection(Block currentBlock) {

		if (!(currentBlock.getBlockData() instanceof Rail rail))
			return direction; // keep current motion

		RailDirection railShape = RailDirection.fromShape(rail.getShape());

		return railShape.getDirection(getCardinalDirection(direction), direction);
	}

	private CardinalDirection getCardinalDirection(Vector dir) {
		double x = dir.getX();
		double z = dir.getZ();

		// If X axis dominates → east/west
		if (Math.abs(x) > Math.abs(z)) {
			return x > 0 ? CardinalDirection.EAST : CardinalDirection.WEST;
		}

		// Otherwise Z axis dominates → north/south
		return z > 0 ? CardinalDirection.SOUTH : CardinalDirection.NORTH;
	}

	private Vector snapToCardinal(Vector v) {
		// SLOPES: keep exact rail-aligned movement (NO normalize)
		if (Math.abs(v.getY()) > 0.1) {
			return new Vector(
				Math.signum(v.getX()),
				Math.signum(v.getY()),
				Math.signum(v.getZ())
			);
		}

		// --- FLAT TRACK ---
		if (Math.abs(v.getX()) > Math.abs(v.getZ()))
			return new Vector(v.getX() > 0 ? 1 : -1, 0, 0);
		else
			return new Vector(0, 0, v.getZ() > 0 ? 1 : -1);
	}

	public float yawFromVector(Vector v) { // yaw in degrees, 0 = south, rotates clockwise
		return (float) Math.toDegrees(Math.atan2(-v.getX(), v.getZ()));
	}

	public float pitchFromVector(Vector v) {
		return (float) -Math.toDegrees(Math.asin(v.getY())); // v must be normalized
	}

	public float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

	public float lerpAngle(float a, float b, float t) {
		float delta = ((b - a + 540) % 360) - 180; // shortest angle difference
		return a + delta * t;
	}

	private enum RailDirection {
		// ----------- STRAIGHT RAILS -----------
		NORTH_SOUTH {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				// Moving NORTH (dz < 0) keeps going NORTH
				// Moving SOUTH (dz > 0) keeps going SOUTH
				return (approach == CardinalDirection.NORTH)
					? new Vector(0, 0, -1)
					: new Vector(0, 0, 1);
			}
		},

		EAST_WEST {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				// Moving EAST (dx > 0) keeps going EAST
				// Moving WEST (dx < 0) keeps going WEST
				return (approach == CardinalDirection.EAST)
					? new Vector(1, 0, 0)
					: new Vector(-1, 0, 0);
			}
		},

		// ----------- SLOPES -----------
		ASCENDING_NORTH {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				// Preserve current horizontal motion, force Y upward or downward
				double z = Math.signum(direction.getZ());
				return new Vector(0, z < 0 ? 1 : -1, z);
			}
		},

		ASCENDING_SOUTH {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				double z = Math.signum(direction.getZ());
				return new Vector(0, z > 0 ? 1 : -1, z);
			}
		},

		ASCENDING_EAST {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				double x = Math.signum(direction.getX());
				return new Vector(x, x > 0 ? 1 : -1, 0);
			}
		},

		ASCENDING_WEST {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				double x = Math.signum(direction.getX());
				return new Vector(x, x < 0 ? 1 : -1, 0);
			}
		},

		// ----------- CURVES -----------
		// Coordinate reminder:
		// X+: EAST, X-: WEST, Z+: SOUTH, Z-: NORTH

		// SOUTH_EAST connects SOUTH <-> EAST
		SOUTH_EAST {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				// Entering from SOUTH side = traveling NORTH into this block → turn EAST
				// Entering from EAST side = traveling WEST into this block → turn SOUTH
				return switch (approach) {
					case NORTH -> new Vector(1, 0, 0);  // go EAST
					case WEST -> new Vector(0, 0, 1);  // go SOUTH
					default -> new Vector(1, 0, 0);  // sane fallback
				};
			}
		},

		// SOUTH_WEST connects SOUTH <-> WEST
		SOUTH_WEST {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				// From SOUTH (moving NORTH) → turn WEST
				// From WEST  (moving EAST)  → turn SOUTH
				return switch (approach) {
					case NORTH -> new Vector(-1, 0, 0); // go WEST
					case EAST -> new Vector(0, 0, 1);  // go SOUTH
					default -> new Vector(-1, 0, 0);
				};
			}
		},

		// NORTH_EAST connects NORTH <-> EAST
		NORTH_EAST {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				// From NORTH (moving SOUTH) → turn EAST
				// From EAST  (moving WEST)  → turn NORTH
				return switch (approach) {
					case SOUTH -> new Vector(1, 0, 0);  // go EAST
					case WEST -> new Vector(0, 0, -1); // go NORTH
					default -> new Vector(1, 0, 0);
				};
			}
		},

		// NORTH_WEST connects NORTH <-> WEST
		NORTH_WEST {
			@Override
			public Vector getDirection(CardinalDirection approach, Vector direction) {
				// From NORTH (moving SOUTH) → turn WEST
				// From WEST  (moving EAST)  → turn NORTH
				return switch (approach) {
					case SOUTH -> new Vector(-1, 0, 0); // go WEST
					case EAST -> new Vector(0, 0, -1); // go NORTH
					default -> new Vector(-1, 0, 0);
				};
			}
		};

		public abstract Vector getDirection(CardinalDirection approach, Vector direction);

		public static RailDirection fromShape(Rail.Shape shape) {
			return valueOf(shape.name());
		}
	}
}
