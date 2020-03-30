package me.pugabyte.bncore.features.particles.effects;

import lombok.Builder;
import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class NyanCatEffect {

	@Builder
	public NyanCatEffect(Player player, int ticks, int startDelay, int pulseDelay) {

		if (player == null) throw new InvalidInputException("No player was provided");

		if (pulseDelay < 1) pulseDelay = 1;
		if (ticks <= 0) ticks = Time.SECOND.x(5);

		int finalTicks = ticks;
		AtomicInteger ticksElapsed = new AtomicInteger(0);
		long millis = System.currentTimeMillis();

		int taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				ParticleUtils.cancelParticle(millis, player);
				return;
			}

			Location loc = player.getLocation();
			for (int i = 0; i < 15; ++i) {
				double[] rgb = ParticleUtils.incRainbow(i);
				double r = rgb[0];
				double g = rgb[1];
				double b = rgb[2];

				loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, r, g, b, 1);

				loc = loc.add(0, 0.1, 0);
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});

		ParticleUtils.addToMap(millis, player, taskId);
	}
}