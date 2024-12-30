package gg.projecteden.nexus.features.particles.effects;

import com.destroystokyo.paper.ParticleBuilder;
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
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class DotEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public DotEffect(HumanEntity player, Location location, boolean clientSide, Particle particle, int count, long ticks, double speed,
					 boolean rainbow, Color color, double disX, double disY, double disZ,
					 int startDelay, int pulseDelay) {

		if (player == null) throw new InvalidInputException("No player was provided");
		if (location == null)
			location = player.getLocation();

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

		if (particle.equals(Particle.DUST)) {
			count = 0;
			speed = 1;
			if (rainbow) {
				disX = 255;
				disY = 0;
				disZ = 0;
			}
		}

		double finalSpeed = speed;
		int finalCount = count;
		long finalTicks = ticks;
		Particle finalParticle = particle;
		Location finalLocation = location;
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

			final ParticleBuilder builder = new ParticleBuilder(finalParticle)
				.location(finalLocation)
				.extra(finalSpeed)
				.count(finalCount)
				.color(red.get(), green.get(), blue.get());

			if (clientSide) {
				if (player instanceof Player)
					builder.receivers((Player) player);
			} else
				builder.allPlayers();

			builder.spawn();

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}

}
