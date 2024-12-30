package gg.projecteden.nexus.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
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

public class DiscoEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public DiscoEffect(HumanEntity player, Location location, boolean updateLoc, Vector updateVector,
					   Direction direction, long ticks, boolean rainbow, RainbowOption rainbowOption,
					   Particle lineParticle, int maxLines, int lineDensity, int lineLength, double lineSpeed, boolean lineRainbow, Color lineColor, double disX1, double disY1, double disZ1,
					   Particle sphereParticle, double sphereRadius, int sphereDensity, double sphereSpeed, boolean sphereRainbow, Color sphereColor, double disX2, double disY2, double disZ2,
					   int startDelay, int pulseDelay) {

		if (player != null && location == null)
			location = player.getLocation();
		if (player == null) throw new InvalidInputException("No player was provided");

		if (lineDensity == 0) lineDensity = 15;
		if (sphereDensity == 0) sphereDensity = 25;
		if (updateVector != null) updateLoc = true;
		if (updateVector == null && updateLoc) updateVector = new Vector(0, 0, 0);
		if (rainbow) {
			lineRainbow = true;
			sphereRainbow = true;
		}

		if (lineRainbow || sphereRainbow)
			if (rainbowOption == null) throw new InvalidInputException("No rainbow option was provided");

		if (lineColor != null) {
			disX1 = lineColor.getRed();
			disY1 = lineColor.getGreen();
			disZ1 = lineColor.getBlue();
		}

		if (sphereColor != null) {
			disX2 = sphereColor.getRed();
			disY2 = sphereColor.getGreen();
			disZ2 = sphereColor.getBlue();
		}

		if (pulseDelay < 1) pulseDelay = 1;
		if (ticks == 0) ticks = TickTime.SECOND.x(5);

		if (lineSpeed <= 0) lineSpeed = 0.1;
		if (lineDensity <= 0) lineDensity = 15;
		if (lineParticle == null) lineParticle = Particle.DUST;

		if (sphereSpeed <= 0) sphereSpeed = 0.1;
		if (sphereDensity <= 0) sphereDensity = 25;
		if (sphereParticle == null) sphereParticle = Particle.DUST;

		int lineCount = lineDensity;
		int sphereCount = sphereDensity;
		if (lineParticle.equals(Particle.DUST)) {
			lineCount = 0;
			lineSpeed = 1;
			if (lineRainbow) {
				disX1 = 255;
				disY1 = 0;
				disZ1 = 0;
			}
		}

		if (sphereParticle.equals(Particle.DUST)) {
			sphereCount = 0;
			sphereSpeed = 1;
			if (sphereRainbow) {
				disX2 = 255;
				disY2 = 0;
				disZ2 = 0;
			}
		}

		double finalLineSpeed = lineSpeed;
		int finalLineDensity = lineDensity;
		Particle finalLineParticle = lineParticle;
		final AtomicDouble lineHue = new AtomicDouble(0);
		final AtomicInteger lineRed = new AtomicInteger((int) disX1);
		final AtomicInteger lineGreen = new AtomicInteger((int) disY1);
		final AtomicInteger lineBlue = new AtomicInteger((int) disZ1);
		boolean finalLineRainbow = lineRainbow;
		int finalLineCount = lineCount;

		double finalSphereSpeed = sphereSpeed;
		int finalSphereDensity = sphereDensity;
		Particle finalSphereParticle = sphereParticle;
		final AtomicDouble sphereHue = new AtomicDouble(0);
		final AtomicInteger sphereRed = new AtomicInteger((int) disX2);
		final AtomicInteger sphereGreen = new AtomicInteger((int) disY2);
		final AtomicInteger sphereBlue = new AtomicInteger((int) disZ2);
		boolean finalSphereRainbow = sphereRainbow;
		int finalSphereCount = sphereCount;

		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		long finalTicks = ticks;
		AtomicInteger ticksElapsed = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				new ParticleService().get(player).cancel(taskId);
				return;
			}

			if (rainbowOption == RainbowOption.SLOW) {
				if (finalLineRainbow)
					incLineRainbow(lineHue, lineRed, lineGreen, lineBlue);
				if (finalSphereRainbow)
					incSphereRainbow(sphereHue, sphereRed, sphereGreen, sphereBlue);
			}

			Location loc = finalLocation;
			if (finalUpdateLoc)
				loc = player.getLocation().add(finalUpdateVector);

			//Lines
			int mL = RandomUtils.getRandom().nextInt(maxLines - 2) + 2;
			for (int m = 0; m < mL * 2; m++) {
				if (rainbowOption == RainbowOption.FAST) {
					if (finalLineRainbow)
						incLineRainbow(lineHue, lineRed, lineGreen, lineBlue);
					if (finalSphereRainbow)
						incSphereRainbow(sphereHue, sphereRed, sphereGreen, sphereBlue);
				}

				double x = RandomUtils.getRandom().nextInt(lineLength - lineLength * (-1)) + lineLength * (-1);
				double y = RandomUtils.getRandom().nextInt(lineLength - lineLength * (-1)) + lineLength * (-1);
				double z = RandomUtils.getRandom().nextInt(lineLength - lineLength * (-1)) + lineLength * (-1);
				if (direction == Direction.DOWN)
					y = RandomUtils.getRandom().nextInt(lineLength * 2 - lineLength) + lineLength;
				else if (direction == Direction.UP)
					y = RandomUtils.getRandom().nextInt(lineLength * (-1) - lineLength * (-2)) + lineLength * (-2);

				Location target = loc.clone().subtract(x, y, z);
				if (target == null) {
					new ParticleService().get(player).cancel(taskId);
					return;
				}

				Vector link = target.toVector().subtract(loc.toVector());
				float length = (float) link.length();
				link.normalize();

				float ratio = length / finalLineDensity;
				Vector v = link.multiply(ratio);
				target = loc.clone().subtract(v);
				for (int i = 0; i < finalLineDensity; i++) {
					if (rainbowOption == RainbowOption.LINE) {
						if (finalLineRainbow)
							incLineRainbow(lineHue, lineRed, lineGreen, lineBlue);
						if (finalSphereRainbow)
							incSphereRainbow(sphereHue, sphereRed, sphereGreen, sphereBlue);
					}
					target.add(v);

					Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalLineParticle, lineRed.get(), lineGreen.get(), lineBlue.get());
					ParticleUtils.display(finalLineParticle, target, finalLineCount, lineRed.get(), lineGreen.get(), lineBlue.get(), finalLineSpeed, dustOptions);
				}
			}

			//Sphere
			for (int i = 0; i < finalSphereDensity; i++) {
				Vector vector = RandomUtils.randomVector().multiply(sphereRadius);
				loc.add(vector);

				Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalSphereParticle, sphereRed.get(), sphereGreen.get(), sphereBlue.get());
				ParticleUtils.display(finalSphereParticle, loc, finalSphereCount, sphereRed.get(), sphereGreen.get(), sphereBlue.get(), finalSphereSpeed, dustOptions);

				loc.subtract(vector);
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}

	private void incSphereRainbow(AtomicDouble sphereHue, AtomicInteger sphereRed, AtomicInteger sphereGreen, AtomicInteger sphereBlue) {
		sphereHue.set(ParticleUtils.incHue(sphereHue.get()));
		int[] rgb = ParticleUtils.incRainbow(sphereHue.get());
		sphereRed.set(rgb[0]);
		sphereGreen.set(rgb[1]);
		sphereBlue.set(rgb[2]);
	}

	private void incLineRainbow(AtomicDouble lineHue, AtomicInteger lineRed, AtomicInteger lineGreen, AtomicInteger lineBlue) {
		lineHue.set(ParticleUtils.incHue(lineHue.get()));
		int[] rgb = ParticleUtils.incRainbow(lineHue.get());
		lineRed.set(rgb[0]);
		lineGreen.set(rgb[1]);
		lineBlue.set(rgb[2]);
	}

	public enum Direction {UP, DOWN, BOTH}

	public enum RainbowOption {SLOW, FAST, LINE}
}
