package me.pugabyte.bncore.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Builder;
import lombok.Getter;
import me.pugabyte.bncore.features.particles.ParticleUtils;
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

public class SphereEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public SphereEffect(Player player, Location location, boolean updateLoc, Vector updateVector, Particle particle,
						boolean rainbow, Color color, int count, int density, double radius, int ticks, double speed,
						double disX, double disY, double disZ, int startDelay, int pulseDelay) {

		if (player != null && location == null)
			location = player.getLocation();
		if (player == null) throw new InvalidInputException("No player was provided");

		if (density == 0) density = 30;
		if (updateVector != null) updateLoc = true;
		if (updateVector == null && updateLoc) updateVector = new Vector(0, 0, 0);

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

		double finalRadius = radius;
		int finalCount = count;
		double finalSpeed = speed;
		int finalTicks = ticks;
		Particle finalParticle = particle;
		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		final AtomicDouble hue = new AtomicDouble(0);
		final AtomicDouble red = new AtomicDouble(disX);
		final AtomicDouble green = new AtomicDouble(disY);
		final AtomicDouble blue = new AtomicDouble(disZ);
		AtomicInteger ticksElapsed = new AtomicInteger(0);
		int finalDensity = density;
		AtomicReference<Double> t = new AtomicReference<>((double) 0);

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
			newLoc.add(0, 1, 0);


			t.updateAndGet(v -> v + Math.PI / (double) finalDensity);

			for (double theta = 0.0D; theta <= Math.PI * 2; theta += Math.PI / (double) finalDensity) {
				double x = finalRadius * Math.cos(theta) * Math.sin(t.get());
				double y = finalRadius * Math.cos(t.get());
				double z = finalRadius * Math.sin(theta) * Math.sin(t.get());
				newLoc.add(x, y, z);
				ParticleUtils.display(finalParticle, newLoc.clone(), finalCount, red.get(), green.get(), blue.get(), finalSpeed);
				newLoc.subtract(x, y, z);
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();

		});
	}
}
