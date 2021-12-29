package gg.projecteden.nexus.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.nexus.features.particles.ParticleUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LineEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public LineEffect(HumanEntity player, Location startLoc, Location endLoc, Particle particle, int count, double density, int ticks, double speed,
					  boolean rainbow, Color color, double disX, double disY, double disZ,
					  double distance, double maxLength, int startDelay, int pulseDelay) {

		if (player != null && startLoc == null)
			startLoc = player.getLocation();
		if (player != null && endLoc == null)
			endLoc = player.getLocation();

		if (player == null) throw new InvalidInputException("No player was provided");

		double maxLineLength = 200;
		if (distance != 0) {
			if (distance > maxLineLength)
				distance = maxLineLength;
			Vector direction = player.getEyeLocation().getDirection();
			startLoc = player.getLocation().add(0, 1.5, 0);
			endLoc = startLoc.clone().add(direction.multiply(distance));
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
		if (maxLength > maxLineLength) maxLength = maxLineLength;
		if (particle == null) particle = Particle.REDSTONE;

		if (particle.equals(Particle.REDSTONE)) {
			count = 0;
			speed = 1;
			if (rainbow) {
				disX = 255;
				disY = 0;
				disZ = 0;
			}
		}

		World world = startLoc.getWorld();
		double diff = startLoc.distance(endLoc);
		if (diff > maxLineLength)
			diff = maxLineLength;

		AtomicReference<Vector> startV = new AtomicReference<>(startLoc.toVector());
		Vector endV = endLoc.toVector();
		Vector vector = endV.clone().subtract(startV.get()).normalize().multiply(density);

		int finalCount = count;
		final AtomicDouble hue = new AtomicDouble(0);
		final AtomicInteger red = new AtomicInteger((int) disX);
		final AtomicInteger green = new AtomicInteger((int) disY);
		final AtomicInteger blue = new AtomicInteger((int) disZ);
		double finalSpeed = speed;
		Location finalStart = startLoc;
		Particle finalParticle = particle;
		int finalTicks = ticks;
		double finalDiff = diff;
		double finalMaxLength = maxLength;
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

			for (double covered = 0; covered < finalDiff; startV.get().add(vector)) {
				Location loc = startV.get().toLocation(world);

				Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalParticle, red.get(), green.get(), blue.get());
				ParticleUtils.display(finalParticle, loc, finalCount, red.get(), green.get(), blue.get(), finalSpeed, dustOptions);

				covered += density;
				if (finalMaxLength != 0 && covered >= finalMaxLength)
					break;
			}

			startV.set(finalStart.toVector());
			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}
}
