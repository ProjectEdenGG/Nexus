package me.pugabyte.bncore.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Builder;
import lombok.Getter;
import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.features.particles.VectorUtils;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PolygonEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public PolygonEffect(Player player, Location location, boolean updateLoc, Vector updateVector, Particle particle,
						 Boolean whole, boolean rotate, double rotateSpeed, Polygon polygon,
						 boolean rainbow, Color color, int count, double radius, int ticks, double speed,
						 double disX, double disY, double disZ, int startDelay, int pulseDelay) {

		if (player != null && location == null)
			location = player.getLocation();
		if (player == null) throw new InvalidInputException("No player was provided");

		if (updateVector != null) updateLoc = true;
		if (updateVector == null && updateLoc) updateVector = new Vector(0, 0, 0);
		if (rotateSpeed != 0 && !rotate) rotate = true;
		if (rotate && rotateSpeed <= 0) rotateSpeed = 0.1;
		if (whole == null) whole = true;

		if (color != null) {
			disX = color.getRed();
			disY = color.getGreen();
			disZ = color.getBlue();
		}

		if (pulseDelay < 1) pulseDelay = 1;
		if (speed <= 0) speed = 0.1;
		if (count <= 0) count = 1;
		if (ticks == 0) ticks = Time.SECOND.x(5);
		if (particle == null) particle = Particle.REDSTONE;
		if (radius <= 0) radius = 1;

		if (particle.equals(Particle.REDSTONE)) {
			count = 0;
			speed = 1;
			if (rainbow) {
				disX = 255;
				disY = 0;
				disZ = 0;
			} else {
				disX /= 255.0;
				disY /= 255.0;
				disZ /= 255.0;
				disX = disX == 0.0 ? 0.001 : disX;
			}
		}

		int points = polygon.getPoints(); // the amount of points the polygon should have.

		Boolean finalWhole = whole;
		boolean finalRotate = rotate;
		double finalRotateSpeed = rotateSpeed;
		double finalRadius = radius;
		int finalCount = count;
		double finalSpeed = speed;
		int finalTicks = ticks;
		Particle finalParticle = particle;
		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		AtomicReference<Double> yRotation = new AtomicReference<>(0.0);
		final AtomicDouble hue = new AtomicDouble(0);
		final AtomicDouble red = new AtomicDouble(disX);
		final AtomicDouble green = new AtomicDouble(disY);
		final AtomicDouble blue = new AtomicDouble(disZ);
		AtomicInteger ticksElapsed = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				((ParticleOwner) new ParticleService().get(player)).cancelTasks(taskId);
				return;
			}

			if (rainbow) {
				hue.set(ParticleUtils.incHue(hue.get()));
				double[] rgb = ParticleUtils.incRainbow(hue.get());
				red.set(rgb[0]);
				green.set(rgb[1]);
				blue.set(rgb[2]);
			}

			Location newLoc = finalLocation;
			if (finalUpdateLoc)
				newLoc = player.getLocation().add(finalUpdateVector);


			for (int iteration = 0; iteration < points; iteration++) {
				double angle = 360.0 / points * iteration;
				double nextAngle = 360.0 / points * (iteration + 1); // the angle for the next point.
				angle = Math.toRadians(angle);
				nextAngle = Math.toRadians(nextAngle); // convert to radians.
				double x = Math.cos(angle) * finalRadius;
				double z = Math.sin(angle) * finalRadius;
				double x2 = Math.cos(nextAngle) * finalRadius;
				double z2 = Math.sin(nextAngle) * finalRadius;
				Vector v1 = new Vector(x, 0, z);
				Vector v2 = new Vector(x2, 0, z2);
				if (finalRotate) {
					VectorUtils.rotateAroundAxisY(v1, yRotation.get() * 0.01745329238474369D);
					VectorUtils.rotateAroundAxisY(v2, yRotation.get() * 0.01745329238474369D);
					yRotation.updateAndGet(v -> v + finalRotateSpeed);
					x = v1.getX();
					z = v1.getZ();
					x2 = v2.getX();
					z2 = v2.getZ();
				}

				Vector link = v1.clone().subtract(v2.clone());
				double distance = link.length() / finalRadius;

				if (finalWhole) {
					double change = .1;
					double sub = -.1;
					switch (points) {
						case 3:
							change = .05;
							sub -= .65;
							break;
						case 4:
							change = .05;
							sub -= .35;
							break;
						case 6:
							sub += .1;
							break;
						case 7:
							sub += .2;
							break;
						case 8:
							sub += .3;
							break;
					}
					for (double d = 0; d < distance + sub; d += change) {
						double finalX = x - link.getX() * d;
						double finalZ = z - link.getZ() * d;
						newLoc.add(finalX, 0, finalZ);
						newLoc.getWorld().spawnParticle(finalParticle, newLoc, finalCount, red.get(), green.get(), blue.get(), finalSpeed);
						newLoc.subtract(finalX, 0, finalZ);
					}
				} else {
					newLoc.add(x, 0, z);
					newLoc.getWorld().spawnParticle(finalParticle, newLoc, finalCount, red.get(), green.get(), blue.get(), finalSpeed);
					newLoc.subtract(x, 0, z);

					newLoc.add(x2, 0, z2);
					newLoc.getWorld().spawnParticle(finalParticle, newLoc, finalCount, red.get(), green.get(), blue.get(), finalSpeed);
					newLoc.subtract(x2, 0, z2);
				}
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();

		});
	}

	@Getter
	public enum Polygon {
		TRIANGLE(3),
		SQUARE(4),
		PENTAGON(5),
		HEXAGON(6),
		HEPTAGON(7),
		OCTAGON(8);

		int points;

		Polygon(int points) {
			this.points = points;
		}
	}
}
