package gg.projecteden.nexus.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.ParticleUtils;
import gg.projecteden.nexus.features.particles.VectorUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PolygonEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public PolygonEffect(HumanEntity player, Location location, boolean updateLoc, Vector updateVector, Particle particle,
						 Boolean whole, boolean rotate, double rotateSpeed, Polygon polygon,
						 boolean rainbow, Color color, int count, double radius, long ticks, double speed,
						 double disX, double disY, double disZ, int startDelay, int pulseDelay,
						 boolean clientSide) {

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
		if (ticks == 0) ticks = TickTime.SECOND.x(5);
		if (particle == null) particle = Particle.DUST;
		if (radius <= 0) radius = 1;

		if (particle.equals(Particle.DUST)) {
			count = 0;
			speed = 1;
			if (rainbow) {
				disX = 255;
				disY = 0;
				disZ = 0;
			}
		}

		int points = polygon.getPoints(); // the amount of points the polygon should have.

		Boolean finalWhole = whole;
		boolean finalRotate = rotate;
		double finalRotateSpeed = rotateSpeed;
		double finalRadius = radius;
		int finalCount = count;
		double finalSpeed = speed;
		long finalTicks = ticks;
		Particle finalParticle = particle;
		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		AtomicReference<Double> yRotation = new AtomicReference<>(0.0);
		final AtomicDouble hue = new AtomicDouble(0);
		final AtomicInteger red = new AtomicInteger((int) disX);
		final AtomicInteger green = new AtomicInteger((int) disY);
		final AtomicInteger blue = new AtomicInteger((int) disZ);
		AtomicInteger ticksElapsed = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				new ParticleService().get(player).cancel(taskId);
				return;
			}

			if (rainbow) {
				hue.set(ParticleUtils.incHue(hue.get()));
				int[] rgb = ParticleUtils.incRainbow(hue.get());
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
						case 3 -> {
							change = .05;
							sub -= .65;
						}
						case 4 -> {
							change = .05;
							sub -= .35;
						}
						case 6 -> sub += .1;
						case 7 -> sub += .2;
						case 8 -> sub += .3;
					}
					for (double d = 0; d < distance + sub; d += change) {
						double finalX = x - link.getX() * d;
						double finalZ = z - link.getZ() * d;
						newLoc.add(finalX, 0, finalZ);

						Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalParticle, red.get(), green.get(), blue.get());
						display(player, clientSide, finalCount, finalSpeed, finalParticle, red, green, blue, newLoc, dustOptions);

						newLoc.subtract(finalX, 0, finalZ);
					}
				} else {
					Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalParticle, red.get(), green.get(), blue.get());

					newLoc.add(x, 0, z);
					display(player, clientSide, finalCount, finalSpeed, finalParticle, red, green, blue, newLoc, dustOptions);
					newLoc.subtract(x, 0, z);

					newLoc.add(x2, 0, z2);
					display(player, clientSide, finalCount, finalSpeed, finalParticle, red, green, blue, newLoc, dustOptions);
					newLoc.subtract(x2, 0, z2);
				}
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();

		});
	}

	private void display(HumanEntity entity, boolean clientSide, int finalCount, double finalSpeed, Particle finalParticle, AtomicInteger red, AtomicInteger green, AtomicInteger blue, Location newLoc, DustOptions dustOptions) {
		if (clientSide) {
			if (entity instanceof Player player)
				ParticleUtils.display(player, finalParticle, newLoc, finalCount, red.get(), green.get(), blue.get(), finalSpeed, dustOptions);
		} else
			ParticleUtils.display(finalParticle, newLoc, finalCount, red.get(), green.get(), blue.get(), finalSpeed, dustOptions);
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
