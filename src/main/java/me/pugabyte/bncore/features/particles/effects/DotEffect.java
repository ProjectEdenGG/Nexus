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

public class DotEffect {

	@Builder
	public DotEffect(Player player, Location loc, Particle particle, int count, int ticks, double speed,
					 boolean rainbow, Color color, double disX, double disY, double disZ,
					 int startDelay, int pulseDelay) {

		if (loc == null) throw new InvalidInputException("No location was provided");

		if (color != null) {
			disX = color.getRed();
			disY = color.getGreen();
			disZ = color.getBlue();
		}

		if (pulseDelay < 1) pulseDelay = 1;
		if (speed <= 0) speed = 0.1;
		if (count <= 0) count = 1;
		if (ticks <= 0) ticks = Time.SECOND.x(5);
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
		long millis = System.currentTimeMillis();

		int taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				ParticleUtils.cancelParticle(millis, player);
				return;
			}

			if (rainbow) {
				double[] rgb = ParticleUtils.incRainbow(red.get(), green.get(), blue.get(), 2.5);
				red.set(rgb[0]);
				green.set(rgb[1]);
				blue.set(rgb[2]);
			}

			loc.getWorld().spawnParticle(finalParticle, loc, finalCount, red.get(), green.get(), blue.get(), finalSpeed);
			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
		ParticleUtils.addToMap(millis, player, taskId);

	}
}
