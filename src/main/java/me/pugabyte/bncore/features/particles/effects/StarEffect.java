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

public class StarEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	StarEffect(Player player, Location location, boolean updateLoc, Vector updateVector, Particle particle, boolean rotate, double rotateSpeed,
			   boolean rainbow, Color color, int count, int density, double radius, int ticks, double speed, double growthSpeed,
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

		boolean finalRotate = rotate;
		double finalRotateSpeed = rotateSpeed;
		double finalDensity = density;

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

		double finalGrowthMin = growthMin;
		double finalGrowthSpeed = growthSpeed;
		boolean finalGrowth = growth;
//		double finalRadius = radius;
		final AtomicDouble growthRadius = new AtomicDouble(radius);

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
					ParticleUtils.display(finalParticle, loc.add(v3), finalCount, red.get(), green.get(), blue.get(), finalSpeed);
				}

				newLoc.subtract(v);
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}
}
