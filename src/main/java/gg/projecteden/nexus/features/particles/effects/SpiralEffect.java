package gg.projecteden.nexus.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.ParticleUtils;
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

public class SpiralEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public SpiralEffect(HumanEntity player, Location location, boolean updateLoc, Vector updateVector, Particle particle,
						boolean rainbow, Color color, int count, double radius, long ticks, double speed,
						double disX, double disY, double disZ, int startDelay, int pulseDelay) {

		if (player != null && location == null)
			location = player.getLocation();
		if (player == null) throw new InvalidInputException("No player was provided");

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

		double finalRadius = radius;
		int finalCount = count;
		double finalSpeed = speed;
		long finalTicks = ticks;
		Particle finalParticle = particle;
		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		AtomicReference<Double> phi = new AtomicReference<>((double) 0);
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

			phi.updateAndGet(v -> v + Math.PI / 16);
			for (double t = 0; t <= 2 * Math.PI; t += Math.PI / 16) {
				for (double i = 0; i < 2; i += 1) {
					double x = finalRadius * (2 * Math.PI - t) * Math.cos(t + phi.get() + i * Math.PI);
					double y = 0.5 * t;
					double z = finalRadius * (2 * Math.PI - t) * Math.sin(t + phi.get() + i * Math.PI);

					newLoc.add(x, y, z);

					Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalParticle, red.get(), green.get(), blue.get());
					ParticleUtils.display(finalParticle, newLoc, finalCount, red.get(), green.get(), blue.get(), finalSpeed, dustOptions);

					newLoc.subtract(x, y, z);
				}
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}
}
