package gg.projecteden.nexus.features.modeltrain;

import gg.projecteden.nexus.utils.ItemBuilder;
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
	private double yOffset = 1.35;
	//
	private List<ArmorStand> cars = new ArrayList<>();
	private ArmorStand engineStand = null;
	private Vector enginePos = null;
	private Vector lateralOffset = new Vector(0, 0, 0);
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
		startLocation.add(0, -yOffset, 0);
		engineStand = world.spawn(startLocation, ArmorStand.class, stand -> {
			stand.setSmall(true);
			stand.setBasePlate(false);
			stand.setArms(false);
			stand.setGravity(false);
			stand.setInvulnerable(true);
			stand.setHelmet(new ItemBuilder(Material.LEATHER_HORSE_ARMOR).model("events/christmas/train_set_engine_1").build());
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
			Block currentBlock = ModelTrainUtils.getRailBlockAtPosition(enginePos, yOffset, world);

			if (currentBlock == null)
				return; // derailed

			// 2. If we entered a NEW rail block, update direction from shape + approach
			if (!currentBlock.equals(previousBlock)) {

				Vector newDirection = ModelTrainUtils.readRailDirection(currentBlock, direction);
				direction = ModelTrainUtils.snapToCardinal(newDirection);

				// LAST
				previousBlock = currentBlock;
			}

			// 3. Move forward along current direction
			moveForward(direction);
			updateHeadPose(direction);
		});
	}

	private void moveForward(Vector direction) {
		// Ensure forward direction is normalized
		Vector forward = direction.clone().normalize();

		Block railBlock = ModelTrainUtils.getRailBlockAtPosition(enginePos, yOffset, world);

		Vector moveVec = forward.clone().multiply(speed);

		if (railBlock != null) {

			// Desired rail center (X/Z only)
			Vector railCenter = new Vector(railBlock.getX() + 0.5, enginePos.getY(), railBlock.getZ() + 0.5);

			// Vector from our position to the center
			Vector toCenter = railCenter.clone().subtract(enginePos);

			// Side axis (perpendicular to forward)
			Vector side = new Vector(-forward.getZ(), 0, forward.getX()).normalize();

			// Signed sideways distance
			double sideDist = toCenter.dot(side);

			// Smooth ONLY the lateral correction
			lateralOffset = lateralOffset.clone().multiply(0.8)    // 0.8 =
				.add(side.clone().multiply(sideDist * 0.2));        // 0.2 =

			// Inject sideways correction INTO movement vector
			moveVec.add(lateralOffset);

			// CRITICAL: clamp total movement back to base speed
			moveVec.normalize().multiply(speed);
		}

		// Apply final speed-corrected movement
		enginePos.add(moveVec);

		// Hard-lock Y to rail surface (anti-derail)
		if (railBlock != null) {
			double surfaceY = ModelTrainUtils.getRailSurfaceY(enginePos, yOffset, railBlock);
			enginePos.setY(surfaceY);
		}

		engineStand.teleport(enginePos.toLocation(world));
	}

	private void updateHeadPose(Vector motion) {
		Vector vector = motion.clone().normalize();

		float targetYaw = ModelTrainUtils.yawFromVector(vector);
		float targetPitch = ModelTrainUtils.pitchFromVector(vector);

		if (!yawInitialized) {
			smoothedYaw = targetYaw;
			smoothedPitch = targetPitch;
			yawInitialized = true;
			return;
		}

		float prevYaw = smoothedYaw;
		float prevPitch = smoothedPitch;

		float yawDelta = ModelTrainUtils.shortestAngleDelta(prevYaw, targetYaw);
		float pitchDelta = targetPitch - prevPitch;

		smoothedYaw = prevYaw + yawDelta * 0.1f;
		smoothedPitch = prevPitch + pitchDelta * 0.25f;

		EulerAngle current = engineStand.getHeadPose();

		double newYaw = current.getY() + Math.toRadians(smoothedYaw - prevYaw);
		double newPitch = current.getX() + Math.toRadians(smoothedPitch - prevPitch);

		engineStand.setHeadPose(new EulerAngle(newPitch, newYaw, current.getZ()));
	}

}
