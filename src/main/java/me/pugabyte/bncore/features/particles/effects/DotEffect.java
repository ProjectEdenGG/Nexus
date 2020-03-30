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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DotEffect {

	@Builder(buildMethodName = "start")
	public DotEffect(Player player, Location loc, Particle particle, int count, int ticks, double speed,
					 boolean rainbow, Color color, double disX, double disY, double disZ,
					 int startDelay, int pulseDelay) {

		if (player == null) throw new InvalidInputException("No player was provided");
		if (loc == null) throw new InvalidInputException("No location was provided");

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
				disX /= 255.0F;
				disY /= 255.0F;
				disZ /= 255.0F;
				disX = disX == 0.0 ? 0.001 : disX;
			}
		}

		double finalSpeed = speed;
		int finalCount = count;
		int finalTicks = ticks;
		Particle finalParticle = particle;
		final AtomicDouble hue = new AtomicDouble(0);
		final AtomicDouble red = new AtomicDouble(disX);
		final AtomicDouble green = new AtomicDouble(disY);
		final AtomicDouble blue = new AtomicDouble(disZ);
		AtomicInteger ticksElapsed = new AtomicInteger(0);
		UUID uuid = UUID.randomUUID();

		int taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				ParticleUtils.cancelParticle(uuid, player);
				return;
			}

			if (rainbow) {
				hue.set(ParticleUtils.incHue(hue.get()));
				double[] rgb = ParticleUtils.incRainbow(hue.get());
				red.set(rgb[0]);
				green.set(rgb[1]);
				blue.set(rgb[2]);
			}

			loc.getWorld().spawnParticle(finalParticle, loc, finalCount, red.get(), green.get(), blue.get(), finalSpeed);
			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
		ParticleUtils.addToMap(uuid, player, taskId);

	}
}
