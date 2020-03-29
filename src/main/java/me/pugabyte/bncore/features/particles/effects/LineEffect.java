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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LineEffect {

	@Builder
	public LineEffect(Player player, Location start, Location end, Particle particle, int count, double density, int ticks, double speed,
					  boolean rainbow, Color color, double disX, double disY, double disZ,
					  double distance, double maxLength, int startDelay, int pulseDelay) {

		if (player == null) throw new InvalidInputException("No player was provided");

		double maxLineLength = 200;
		if (distance != 0) {
			if (distance > maxLineLength) distance = maxLineLength;
			Vector direction = player.getEyeLocation().getDirection();
			start = player.getLocation().add(0, 1.5, 0);
			end = start.clone().add(direction.multiply(distance));
		}

		if (start == null) throw new InvalidInputException("No start location was provided");
		if (end == null) throw new InvalidInputException("No end location was provided");

		if (color != null) {
			disX = color.getRed();
			disY = color.getGreen();
			disZ = color.getBlue();
		}

		if (pulseDelay < 1) pulseDelay = 1;
		if (speed <= 0) speed = 0.1;
		if (count <= 0) count = 1;
		if (ticks == 0) ticks = Time.SECOND.x(5);
		if (maxLength > maxLineLength) maxLength = maxLineLength;
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

		World world = start.getWorld();
		double diff = start.distance(end);
		if (diff > maxLineLength)
			diff = maxLineLength;

		AtomicReference<Vector> startV = new AtomicReference<>(start.toVector());
		Vector endV = end.toVector();
		Vector vector = endV.clone().subtract(startV.get()).normalize().multiply(density);

		int finalCount = count;
		final AtomicDouble red = new AtomicDouble(disX);
		final AtomicDouble green = new AtomicDouble(disY);
		final AtomicDouble blue = new AtomicDouble(disZ);
		double finalSpeed = speed;
		Location finalStart = start;
		Particle finalParticle = particle;
		int finalTicks = ticks;
		double finalDiff = diff;
		double finalMaxLength = maxLength;
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

			for (double covered = 0; covered < finalDiff; startV.get().add(vector)) {
				Location loc = startV.get().toLocation(world);
				world.spawnParticle(finalParticle, loc, finalCount, red.get(), green.get(), blue.get(), finalSpeed);
				covered += density;
				if (finalMaxLength != 0 && covered >= finalMaxLength)
					break;
			}

			startV.set(finalStart.toVector());
			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});

		ParticleUtils.addToMap(millis, player, taskId);
	}
}
