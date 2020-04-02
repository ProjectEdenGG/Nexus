package me.pugabyte.bncore.features.particles.effects;

import lombok.Builder;
import lombok.Getter;
import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.particleeffect.EffectOwner;
import me.pugabyte.bncore.models.particleeffect.EffectService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class NyanCatEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public NyanCatEffect(Player player, int ticks, int startDelay, int pulseDelay) {

		if (player == null) throw new InvalidInputException("No player was provided");

		if (pulseDelay < 1) pulseDelay = 1;
		if (ticks == 0) ticks = Time.SECOND.x(5);

		int finalTicks = ticks;
		AtomicInteger ticksElapsed = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				((EffectOwner) new EffectService().get(player)).cancelTasks(taskId);
				return;
			}

			Location loc = player.getLocation();
			Vector backward = player.getEyeLocation().getDirection().multiply(0.5);
			loc = loc.subtract(backward);
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
	}
}