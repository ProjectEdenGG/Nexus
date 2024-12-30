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
import org.bukkit.entity.HumanEntity;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class StarEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	StarEffect(HumanEntity player, Location location, boolean updateLoc, Vector updateVector, Particle particle, boolean rotate, double rotateSpeed,
			   boolean rainbow, Color color, int count, int density, double radius, long ticks, double speed, double growthSpeed,
			   double disX, double disY, double disZ, int startDelay, int pulseDelay) {

		if (player != null && location == null)
			location = player.getLocation();
		if (player == null) throw new InvalidInputException("No player was provided");

		if (density == 0) density = 20;
		if (updateVector != null) updateLoc = true;
		if (updateVector == null && updateLoc) updateVector = new Vector(0, 0, 0);
		if (rotateSpeed != 0 && !rotate) rotate = true;
		if (rotate && rotateSpeed <= 0) rotateSpeed = 0.1;
		boolean growth = false;
		double growthMax = radius;
		double growthMin = 0;
		if (growthSpeed <= 0) {
			growthMin = growthMax;
			growthSpeed = growthMax;
		} else {
			growth = true;
		}

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

		boolean finalRotate = rotate;
		double finalRotateSpeed = rotateSpeed;
		double finalDensity = density;

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

		double finalGrowthMin = growthMin;
		double finalGrowthSpeed = growthSpeed;
		boolean finalGrowth = growth;
//		double finalRadius = radius;
		final AtomicDouble growthRadius = new AtomicDouble(radius);

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

			double angleForward = 2.5132741928100586D;

			if (finalGrowth) {
				growthRadius.set(growthRadius.get() + finalGrowthSpeed);
				if (growthRadius.get() >= growthMax)
					growthRadius.set(finalGrowthMin);
			}

			for (int i = 1; i < 6; ++i) {
				double angleY = (float) i * 1.2566371F;
				double x = Math.cos(angleY) * growthRadius.get();
				double z = Math.sin(angleY) * growthRadius.get();
				Vector v = new Vector(x, 0, z);
				Vector star = v.clone();
				VectorUtils.rotateAroundAxisY(star, angleForward);
				if (finalRotate) {
					VectorUtils.rotateAroundAxisY(v, yRotation.get() * 0.01745329238474369D);
					VectorUtils.rotateAroundAxisY(star, yRotation.get() * 0.01745329238474369D);
					yRotation.updateAndGet(v1 -> v1 + finalRotateSpeed);
				}

				newLoc.add(v);
				Vector link = star.clone().subtract(v.clone());
				float length = (float) link.length();
				link.normalize();
				float ratio = length / (float) finalDensity;
				Vector v3 = link.multiply(ratio);
				Location loc = newLoc.clone().subtract(v3);

				for (int i2 = 0; i2 < finalDensity; ++i2) {
					Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalParticle, red.get(), green.get(), blue.get());
					ParticleUtils.display(finalParticle, loc.add(v3), finalCount, red.get(), green.get(), blue.get(), finalSpeed, dustOptions);
				}

				newLoc.subtract(v);
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}
}
