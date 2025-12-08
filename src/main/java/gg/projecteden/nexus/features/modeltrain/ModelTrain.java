package gg.projecteden.nexus.features.modeltrain;

import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
	private Vector railCenterTarget = null;
	private float smoothedYaw = 0f;
	private float smoothedPitch = 0f;
	private boolean yawInitialized = false;

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
		startLocation.setPitch(0);
		engineStand = world.spawn(startLocation, ArmorStand.class, stand -> {
			stand.setGravity(false);
			stand.setInvulnerable(true);
			stand.setHelmet(new ItemStack(Material.PLAYER_HEAD));
		});

		// TODO: FACING WRONG DIRECTION ON SPAWN IN SOME DIRECTIONS
		// Start pointing in some direction, then snap to cardinal
		direction = ModelTrainUtils.snapToCardinal(startLocation.getDirection());

		// Start position as a vector
		enginePos = startLocation.toVector();

		active = true;
		task = Tasks.repeat(0, 1, () -> {
			if (!active)
				return;

			// 1. Find the rail block we're currently on
			Block currentBlock = ModelTrainUtils.getRailBlockAtPosition(enginePos, world);

			if (currentBlock == null)
				return; // derailed

			// 2. If we entered a NEW rail block, update direction from shape + approach
			if (!currentBlock.equals(previousBlock)) {

				Vector newDirection = ModelTrainUtils.readRailDirection(currentBlock, direction);
				direction = ModelTrainUtils.snapToCardinal(newDirection);

				// Snap to block center on X/Z when we first enter it
				enginePos.setX(currentBlock.getX() + 0.5);
				enginePos.setZ(currentBlock.getZ() + 0.5);
				enginePos.setY(currentBlock.getY() + 0.1);

				// LAST
				previousBlock = currentBlock;
			}

			// 3. Move forward along current direction
			moveForward(direction);
			updateHeadPose(direction);
		});
	}

	private void moveForward(Vector direction) {

		// Move forward in X/Y/Z
		enginePos.add(direction.clone().multiply(speed));

		// Attach to the actual rail surface EVERY TICK
		Block railBlock = ModelTrainUtils.getRailBlockAtPosition(enginePos, world);
		if (railBlock != null) {
			double surfaceY = ModelTrainUtils.getRailSurfaceY(enginePos, railBlock);
			enginePos.setY(surfaceY);
		}

		engineStand.teleport(enginePos.toLocation(world));
	}

	private void updateHeadPose(Vector motion) {

		Vector v = motion.clone().normalize();

		float targetYaw = ModelTrainUtils.yawFromVector(v);
		float targetPitch = ModelTrainUtils.pitchFromVector(v);

		if (!yawInitialized) {
			smoothedYaw = targetYaw;
			smoothedPitch = targetPitch;
			yawInitialized = true;
			return;
		}

		float prevYaw = smoothedYaw;
		float prevPitch = smoothedPitch;

		// shortest-arc delta in continuous space
		float yawDelta = ModelTrainUtils.shortestAngleDelta(prevYaw, targetYaw);
		float pitchDelta = targetPitch - prevPitch;

		// Apply smoothing to deltas
		smoothedYaw = prevYaw + yawDelta * 0.2f;
		smoothedPitch = prevPitch + pitchDelta * 0.25f;

		EulerAngle current = engineStand.getHeadPose();

		double newYaw = current.getY() + Math.toRadians(smoothedYaw - prevYaw);
		double newPitch = current.getX() + Math.toRadians(smoothedPitch - prevPitch);

		engineStand.setHeadPose(new EulerAngle(
			newPitch,
			newYaw,
			current.getZ()
		));
	}

}
