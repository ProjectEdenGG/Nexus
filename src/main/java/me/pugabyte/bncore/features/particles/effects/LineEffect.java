package me.pugabyte.bncore.features.particles.effects;

import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LineEffect {

	public LineEffect(Player player, int distance, double density) {
		Vector direction = player.getEyeLocation().getDirection();
		Location start = player.getLocation().add(0, 1.5, 0);
		Location end = start.clone().add(direction.multiply(distance));
		new LineEffect(player, start, end, Particle.REDSTONE, 1, density, 10 * 20, 0, 0, 0);
	}

	// possible variables to add:
	// pulse delay --> task interval
	// length --> max distance
	// start delay --> task start delay
	// particle delay --> delay between spawning particles
	//
	public LineEffect(Player player, Location start, Location end, Particle particle, int count, double density, int ticks, double red, double green, double blue) {
		double speed = 0.1;
		if (particle.equals(Particle.REDSTONE)) {
			count = 0;
			red /= 255.0;
			green /= 255.0;
			blue /= 255.0;
			speed = 1;
		}

		World world = start.getWorld();
		double distance = start.distance(end);
		AtomicReference<Vector> startV = new AtomicReference<>(start.toVector());
		Vector endV = end.toVector();
		Vector vector = endV.clone().subtract(startV.get()).normalize().multiply(density);

		int finalCount = count;
		double finalRed = red;
		double finalGreen = green;
		double finalBlue = blue;
		double finalSpeed = speed;
		AtomicInteger ticksElapsed = new AtomicInteger(0);
		long millis = System.currentTimeMillis();

		// Draws a solid line from one point to another
		int taskId = Tasks.repeat(0, 1, () -> {
			if (ticksElapsed.get() >= ticks) {
				ParticleUtils.cancelParticle(millis, player);
				return;
			}

			for (double length = 0; length < distance; startV.get().add(vector)) {
				Location location = startV.get().toLocation(world);
				world.spawnParticle(particle, location, finalCount, finalRed, finalGreen, finalBlue, finalSpeed);
				length += density;
			}

			startV.set(start.toVector());
			ticksElapsed.incrementAndGet();
		});

		ParticleUtils.addToMap(millis, player, taskId);
	}
}
