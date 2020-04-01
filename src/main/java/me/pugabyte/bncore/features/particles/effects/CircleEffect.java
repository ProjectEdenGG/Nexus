package me.pugabyte.bncore.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Builder;
import lombok.Getter;
import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.features.particles.VectorUtils;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class CircleEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public CircleEffect(Player player, Location location, boolean updateLoc, Vector updateVector, Particle particle, boolean whole, boolean randomRotation,
						boolean rainbow, Color color, int count, int density, int ticks, double radius, double speed, boolean fast,
						double disX, double disY, double disZ, int startDelay, int pulseDelay) {

		if (player == null) throw new InvalidInputException("No player was provided");
		if (location == null) throw new InvalidInputException("No location was provided");

		if (density == 0) density = 20;
		double inc = (2 * Math.PI) / density;
		int steps = whole ? density : 1;
		int loops = 1;
		if (fast) loops = 10;
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

		double angularVelocityX = Math.PI / 200;
		double angularVelocityY = Math.PI / 170;
		double angularVelocityZ = Math.PI / 155;

		double finalSpeed = speed;
		int finalCount = count;
		int finalTicks = ticks;
		Particle finalParticle = particle;
		int finalLoops = loops;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		final AtomicDouble hue = new AtomicDouble(0);
		final AtomicDouble red = new AtomicDouble(disX);
		final AtomicDouble green = new AtomicDouble(disY);
		final AtomicDouble blue = new AtomicDouble(disZ);
		AtomicInteger ticksElapsed = new AtomicInteger(0);
		AtomicInteger step = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				ParticleUtils.cancelEffectTask(taskId);
				return;
			}

			for (int j = 0; j < finalLoops; j++) {
				if (rainbow) {
					hue.set(ParticleUtils.incHue(hue.get()));
					double[] rgb = ParticleUtils.incRainbow(hue.get());
					red.set(rgb[0]);
					green.set(rgb[1]);
					blue.set(rgb[2]);
				}

				Location loc = location;
				if (finalUpdateLoc)
					loc = player.getLocation().add(finalUpdateVector);

				for (int i = 0; i < steps; i++) {
					double angle = step.get() * inc;
					Vector v = new Vector();
					v.setX(Math.cos(angle) * radius);
					v.setZ(Math.sin(angle) * radius);
					if (randomRotation)
						VectorUtils.rotateVector(v, angularVelocityX * step.get(), angularVelocityY * step.get(), angularVelocityZ * step.get());
					loc.getWorld().spawnParticle(finalParticle, loc.clone().add(v), finalCount, red.get(), green.get(), blue.get(), finalSpeed);
					step.getAndIncrement();
				}
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}
}
