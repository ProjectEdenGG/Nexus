package gg.projecteden.nexus.features.particles.effects;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.ParticleUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.HumanEntity;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class StormEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public StormEffect(HumanEntity player, Location location, boolean updateLoc, Vector updateVector, RainPartile rainParticle,
					   int density, double radius, long ticks, int startDelay, int pulseDelay) {

		Particle cloudParticle = Particle.DUST;
		Color cloudColor = Color.fromRGB(127, 127, 127);
		int cloudRed = cloudColor.getRed();
		int cloudGreen = cloudColor.getGreen();
		int cloudBlue = cloudColor.getBlue();

		if (player != null && location == null)
			location = player.getLocation();
		if (player == null) throw new InvalidInputException("No player was provided");

		if (updateVector != null) updateLoc = true;
		if (updateVector == null && updateLoc) updateVector = new Vector(0, 0, 0);

		if (density == 0) density = 60;
		if (pulseDelay < 1) pulseDelay = 1;
		if (ticks == 0) ticks = TickTime.SECOND.x(5);
		if (rainParticle == null) rainParticle = RainPartile.RAIN;
		if (radius <= 0) radius = 1.5;

		double rainRadius = radius - 0.25;
		double finalCloudRadius = radius;
		int finalDensity = density;
		long finalTicks = ticks;
		RainPartile finalRainParticle = rainParticle;
		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		AtomicInteger ticksElapsed = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				new ParticleService().get(player).cancel(taskId);
				return;
			}

			Location newLoc = finalLocation;
			if (finalUpdateLoc)
				newLoc = player.getLocation().add(finalUpdateVector);
			newLoc.add(0, 3.2, 0);

			for (int i = 0; i < finalDensity; i++) {
				Vector v = RandomUtils.randomCircleVector().multiply(RandomUtils.getRandom().nextDouble() * finalCloudRadius);
				newLoc.add(v);

				Particle.DustOptions dustOptions = ParticleUtils.newDustOption(cloudParticle, cloudRed, cloudGreen, cloudBlue);
				ParticleUtils.display(cloudParticle, newLoc.clone(), 0, cloudRed, cloudGreen, cloudBlue, 1, dustOptions);

				newLoc.subtract(v);
			}

			Location l1 = newLoc.clone().add(0, 0.2, 0);
			for (int i = 0; i < finalDensity; i++) {
				Vector v = RandomUtils.randomCircleVector().multiply(RandomUtils.getRandom().nextDouble() * (finalCloudRadius - 0.25));
				l1.add(v);

				Particle.DustOptions dustOptions = ParticleUtils.newDustOption(cloudParticle, cloudRed, cloudGreen, cloudBlue);
				ParticleUtils.display(cloudParticle, l1.clone(), 0, cloudRed, cloudGreen, cloudBlue, 1, dustOptions);

				l1.subtract(v);
			}

			Location l2 = newLoc.clone().add(0, .05, 0);
			for (int i = 0; i < 15; i++) {
				Vector v = RandomUtils.randomCircleVector().multiply(RandomUtils.getRandom().nextDouble() * rainRadius);
				l2.add(v);

				ParticleUtils.display(finalRainParticle.getParticle(), l2.clone(), 0, 0, 0, 0, 0.1, null);

				l2.subtract(v);
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}

	@Getter
	public enum RainPartile {
		RAIN(Particle.DRIPPING_WATER),
		SNOW(Particle.ITEM_SNOWBALL),
		SLIME(Particle.ITEM_SLIME),
		SYMBOLS(Particle.ENCHANT);

		Particle particle;

		RainPartile(Particle particle) {
			this.particle = particle;
		}
	}
}
