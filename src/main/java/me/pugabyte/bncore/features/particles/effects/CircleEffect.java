package me.pugabyte.bncore.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Builder;
import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class CircleEffect {

	@Builder
	public CircleEffect(Player player, Location location, boolean updateLoc, Particle particle, boolean whole,
						boolean rainbow, Color color, int count, int density, int ticks, double radius, double speed,
						double disX, double disY, double disZ, int startDelay, int pulseDelay) {

		if (location == null) throw new InvalidInputException("No location was provided");

		if (density == 0) density = 20;
		double inc = (2 * Math.PI) / density;
		int steps = whole ? density : 1;

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

		if (particle.equals(Particle.REDSTONE)) {
			count = 0;
			speed = 1;
			if (rainbow) {
				disX = 1;
				disY = 0;
				disZ = 0;
			} else {
				disX /= 255.0;
				disY /= 255.0;
				disZ /= 255.0;
			}
		}

		double finalSpeed = speed;
		int finalCount = count;
		int finalTicks = ticks;
		Particle finalParticle = particle;
		final AtomicDouble red = new AtomicDouble(disX);
		final AtomicDouble green = new AtomicDouble(disY);
		final AtomicDouble blue = new AtomicDouble(disZ);
		AtomicInteger ticksElapsed = new AtomicInteger(0);
		AtomicInteger step = new AtomicInteger();
		long millis = System.currentTimeMillis();

		int taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				ParticleUtils.cancelParticle(millis, player);
				return;
			}

			if (rainbow) {
				double[] rgb = ParticleUtils.incRainbow(red.get(), green.get(), blue.get(), 9);
				red.set(rgb[0]);
				green.set(rgb[1]);
				blue.set(rgb[2]);
			}

			Location loc = location;
			if (updateLoc)
				loc = player.getLocation();

			for (int i = 0; i < steps; i++) {
				double angle = step.get() * inc;
				double x = Math.cos(angle) * radius;
				double z = Math.sin(angle) * radius;
				loc.add(x, 0, z);
				loc.getWorld().spawnParticle(finalParticle, loc, finalCount, red.get(), green.get(), blue.get(), finalSpeed);
				loc.subtract(x, 0, z);
				step.getAndIncrement();
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});

		ParticleUtils.addToMap(millis, player, taskId);
	}
}
