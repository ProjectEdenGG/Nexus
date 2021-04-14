package me.pugabyte.nexus.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Builder;
import lombok.Getter;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class DotEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public DotEffect(Player player, Location location, Particle particle, int count, int ticks, double speed,
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
		if (ticks == 0) ticks = Time.SECOND.x(5);
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

		double finalSpeed = speed;
		int finalCount = count;
		int finalTicks = ticks;
		Particle finalParticle = particle;
		Location finalLocation = location;
		final AtomicDouble hue = new AtomicDouble(0);
		final AtomicInteger red = new AtomicInteger((int) disX);
		final AtomicInteger green = new AtomicInteger((int) disY);
		final AtomicInteger blue = new AtomicInteger((int) disZ);
		AtomicInteger ticksElapsed = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				((ParticleOwner) new ParticleService().get(player)).cancelTasks(taskId);
				return;
			}

			if (rainbow) {
				hue.set(ParticleUtils.incHue(hue.get()));
				int[] rgb = ParticleUtils.incRainbow(hue.get());
				red.set(rgb[0]);
				green.set(rgb[1]);
				blue.set(rgb[2]);
			}

			Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalParticle, red.get(), green.get(), blue.get());
			ParticleUtils.display(finalParticle, finalLocation, finalCount, red.get(), green.get(), blue.get(), finalSpeed, dustOptions);

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}
}
