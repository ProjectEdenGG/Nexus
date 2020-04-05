package me.pugabyte.bncore.features.particles.effects;

import lombok.Builder;
import lombok.Getter;
import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.features.particles.RandomUtils;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class StormEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public StormEffect(Player player, Location location, boolean updateLoc, Vector updateVector, RainPartile rainParticle,
					   int density, double radius, int ticks, int startDelay, int pulseDelay) {

		Particle cloudParticle = Particle.REDSTONE;
		Color cloudColor = Color.fromRGB(127, 127, 127);
		double cloudRed = cloudColor.getRed() / 255.0;
		double cloudGreen = cloudColor.getGreen() / 255.0;
		double cloudBlue = cloudColor.getBlue() / 255.0;

		if (player != null && location == null)
			location = player.getLocation();
		if (player == null) throw new InvalidInputException("No player was provided");

		if (updateVector != null) updateLoc = true;
		if (updateVector == null && updateLoc) updateVector = new Vector(0, 0, 0);

		if (density == 0) density = 60;
		if (pulseDelay < 1) pulseDelay = 1;
		if (ticks == 0) ticks = Time.SECOND.x(5);
		if (rainParticle == null) rainParticle = RainPartile.RAIN;
		if (radius <= 0) radius = 1.5;

		double rainRadius = radius - 0.25;
		double finalCloudRadius = radius;
		int finalDensity = density;
		int finalTicks = ticks;
		RainPartile finalRainParticle = rainParticle;
		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		AtomicInteger ticksElapsed = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				((ParticleOwner) new ParticleService().get(player)).cancelTasks(taskId);
				return;
			}

			Location newLoc = finalLocation;
			if (finalUpdateLoc)
				newLoc = player.getLocation().add(finalUpdateVector);
			newLoc.add(0, 3.2, 0);

			for (int i = 0; i < finalDensity; i++) {
				Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * finalCloudRadius);
				newLoc.add(v);
				ParticleUtils.display(cloudParticle, newLoc.clone(), 0, cloudRed, cloudGreen, cloudBlue, 1);
				newLoc.subtract(v);
			}

			Location l1 = newLoc.clone().add(0, 0.2, 0);
			for (int i = 0; i < finalDensity; i++) {
				Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * (finalCloudRadius - 0.25));
				l1.add(v);
				ParticleUtils.display(cloudParticle, l1.clone(), 0, cloudRed, cloudGreen, cloudBlue, 1);
				l1.subtract(v);
			}

			Location l2 = newLoc.clone().add(0, .05, 0);
			for (int i = 0; i < 15; i++) {
				Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * rainRadius);
				l2.add(v);
				ParticleUtils.display(finalRainParticle.getParticle(), l2.clone(), 0, 0, 0, 0, 0.1);
				l2.subtract(v);
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}

	@Getter
	public enum RainPartile {
		RAIN(Particle.WATER_DROP),
		SNOW(Particle.SNOW_SHOVEL),
		SLIME(Particle.SLIME),
		SYMBOLS(Particle.ENCHANTMENT_TABLE);

		Particle particle;

		RainPartile(Particle particle) {
			this.particle = particle;
		}
	}
}
