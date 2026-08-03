package gg.projecteden.nexus.features.mcmmo.resetnew.skills.crossbows.turret;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class TurretTrajectory {

	private static final double CROSSBOW_VELOCITY = 3.15;
	private static final double DRAG = 0.99;
	private static final double GRAVITY = 0.05;

	private static final int MAX_SIMULATION_TICKS = 200;
	private static final double PITCH_STEP = 0.25;
	private static final int PITCH_REFINEMENT_STEPS = 20;

	public static AimResult aim(Location origin, LivingEntity target) {
		Location targetLocation = target.getLocation().add(0, target.getHeight() * 0.5, 0);
		return aim(origin, targetLocation);
	}

	public static AimResult aim(Location origin, Location target) {
		if (origin.getWorld() != target.getWorld())
			return null;

		Vector difference = target.toVector().subtract(origin.toVector());
		double horizontalDistance = Math.hypot(difference.getX(), difference.getZ());

		if (horizontalDistance < 0.001)
			return null;

		double yaw = Math.toDegrees(Math.atan2(-difference.getX(), difference.getZ()));
		double directPitch = Math.toDegrees(Math.atan2(-difference.getY(), horizontalDistance));

		double previousPitch = directPitch;
		double previousError = calculateVerticalError(origin, target, yaw, previousPitch);

		for (double adjustment = PITCH_STEP; adjustment <= 89; adjustment += PITCH_STEP) {
			double pitch = directPitch - adjustment;

			if (pitch < -89)
				break;

			double error = calculateVerticalError(origin, target, yaw, pitch);

			if (!Double.isFinite(error))
				continue;

			if (Double.isFinite(previousError) && Math.signum(error) != Math.signum(previousError)) {
				double solvedPitch = refinePitch(origin, target, yaw, pitch, previousPitch);
				Vector velocity = direction(yaw, solvedPitch).multiply(CROSSBOW_VELOCITY);

				return new AimResult((float) yaw, (float) solvedPitch, velocity);
			}

			previousPitch = pitch;
			previousError = error;
		}

		return null;
	}

	private static double refinePitch(Location origin, Location target, double yaw, double pitchA, double pitchB) {
		double errorA = calculateVerticalError(origin, target, yaw, pitchA);

		for (int i = 0; i < PITCH_REFINEMENT_STEPS; i++) {
			double middlePitch = (pitchA + pitchB) / 2;
			double middleError = calculateVerticalError(origin, target, yaw, middlePitch);

			if (Math.signum(errorA) == Math.signum(middleError)) {
				pitchA = middlePitch;
				errorA = middleError;
			} else
				pitchB = middlePitch;
		}

		return (pitchA + pitchB) / 2;
	}

	private static double calculateVerticalError(Location origin, Location target, double yaw, double pitch) {
		Vector targetOffset = target.toVector().subtract(origin.toVector());
		Vector horizontalDirection = targetOffset.clone().setY(0).normalize();

		double targetHorizontalDistance = Math.hypot(targetOffset.getX(), targetOffset.getZ());
		double targetY = targetOffset.getY();

		Vector position = new Vector();
		Vector velocity = direction(yaw, pitch).multiply(CROSSBOW_VELOCITY);

		Vector previousPosition = position.clone();

		for (int tick = 0; tick < MAX_SIMULATION_TICKS; tick++) {
			previousPosition.copy(position);
			position.add(velocity);

			double previousHorizontal = previousPosition.dot(horizontalDirection);
			double currentHorizontal = position.dot(horizontalDirection);

			if (currentHorizontal >= targetHorizontalDistance) {
				double travelled = currentHorizontal - previousHorizontal;
				double progress = travelled == 0 ? 0 : (targetHorizontalDistance - previousHorizontal) / travelled;
				double arrowY = previousPosition.getY() + (position.getY() - previousPosition.getY()) * progress;

				return arrowY - targetY;
			}

			velocity.multiply(DRAG);
			velocity.setY(velocity.getY() - GRAVITY);

			if (position.getY() < targetY - 100)
				break;
		}

		return Double.NaN;
	}

	private static Vector direction(double yaw, double pitch) {
		double yawRadians = Math.toRadians(yaw);
		double pitchRadians = Math.toRadians(pitch);
		double horizontal = Math.cos(pitchRadians);

		return new Vector(
			-Math.sin(yawRadians) * horizontal,
			-Math.sin(pitchRadians),
			Math.cos(yawRadians) * horizontal
		);
	}

	public record AimResult(float yaw, float pitch, Vector velocity) { }
}
