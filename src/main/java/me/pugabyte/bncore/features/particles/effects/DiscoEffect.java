package me.pugabyte.bncore.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Builder;
import lombok.Getter;
import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.features.particles.RandomUtils;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.particleeffect.EffectOwner;
import me.pugabyte.bncore.models.particleeffect.EffectService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class DiscoEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public DiscoEffect(Player player, Location location, boolean updateLoc, Vector updateVector,
					   Direction direction, int ticks, boolean rainbow, RainbowOption rainbowOption,
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
		if (ticks == 0) ticks = Time.SECOND.x(5);

		if (lineSpeed <= 0) lineSpeed = 0.1;
		if (lineDensity <= 0) lineDensity = 15;
		if (lineParticle == null) lineParticle = Particle.REDSTONE;

		if (sphereSpeed <= 0) sphereSpeed = 0.1;
		if (sphereDensity <= 0) sphereDensity = 25;
		if (sphereParticle == null) sphereParticle = Particle.REDSTONE;

		int lineCount = lineDensity;
		int sphereCount = sphereDensity;
		if (lineParticle.equals(Particle.REDSTONE)) {
			lineCount = 0;
			lineSpeed = 1;
			if (lineRainbow) {
				disX1 = 255;
				disY1 = 0;
				disZ1 = 0;
			} else {
				disX1 /= 255.0;
				disY1 /= 255.0;
				disZ1 /= 255.0;
				disX1 = disX1 == 0.0 ? 0.001 : disX1;
			}
		}

		if (sphereParticle.equals(Particle.REDSTONE)) {
			sphereCount = 0;
			sphereSpeed = 1;
			if (sphereRainbow) {
				disX2 = 255;
				disY2 = 0;
				disZ2 = 0;
			} else {
				disX2 /= 255.0;
				disY2 /= 255.0;
				disZ2 /= 255.0;
				disX2 = disX2 == 0.0 ? 0.001 : disX2;
			}
		}

		double finalLineSpeed = lineSpeed;
		int finalLineDensity = lineDensity;
		Particle finalLineParticle = lineParticle;
		final AtomicDouble lineHue = new AtomicDouble(0);
		final AtomicDouble lineRed = new AtomicDouble(disX1);
		final AtomicDouble lineGreen = new AtomicDouble(disY1);
		final AtomicDouble lineBlue = new AtomicDouble(disZ1);
		boolean finalLineRainbow = lineRainbow;
		int finalLineCount = lineCount;

		double finalSphereSpeed = sphereSpeed;
		int finalSphereDensity = sphereDensity;
		Particle finalSphereParticle = sphereParticle;
		final AtomicDouble sphereHue = new AtomicDouble(0);
		final AtomicDouble sphereRed = new AtomicDouble(disX2);
		final AtomicDouble sphereGreen = new AtomicDouble(disY2);
		final AtomicDouble sphereBlue = new AtomicDouble(disZ2);
		boolean finalSphereRainbow = sphereRainbow;
		int finalSphereCount = sphereCount;

		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		int finalTicks = ticks;
		AtomicInteger ticksElapsed = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				((EffectOwner) new EffectService().get(player)).cancelTasks(taskId);
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
			int mL = RandomUtils.random.nextInt(maxLines - 2) + 2;
			for (int m = 0; m < mL * 2; m++) {
				if (rainbowOption == RainbowOption.FAST) {
					if (finalLineRainbow)
						incLineRainbow(lineHue, lineRed, lineGreen, lineBlue);
					if (finalSphereRainbow)
						incSphereRainbow(sphereHue, sphereRed, sphereGreen, sphereBlue);
				}

				double x = RandomUtils.random.nextInt(lineLength - lineLength * (-1)) + lineLength * (-1);
				double y = RandomUtils.random.nextInt(lineLength - lineLength * (-1)) + lineLength * (-1);
				double z = RandomUtils.random.nextInt(lineLength - lineLength * (-1)) + lineLength * (-1);
				if (direction == Direction.DOWN)
					y = RandomUtils.random.nextInt(lineLength * 2 - lineLength) + lineLength;
				else if (direction == Direction.UP)
					y = RandomUtils.random.nextInt(lineLength * (-1) - lineLength * (-2)) + lineLength * (-2);

				Location target = loc.clone().subtract(x, y, z);
				if (target == null) {
					((EffectOwner) new EffectService().get(player)).cancelTasks(taskId);
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
					target.getWorld().spawnParticle(finalLineParticle, target, finalLineCount, lineRed.get(), lineGreen.get(), lineBlue.get(), finalLineSpeed);
				}
			}

			//Sphere
			for (int i = 0; i < finalSphereDensity; i++) {
				Vector vector = RandomUtils.getRandomVector().multiply(sphereRadius);
				loc.add(vector);
				loc.getWorld().spawnParticle(finalSphereParticle, loc, finalSphereCount, sphereRed.get(), sphereGreen.get(), sphereBlue.get(), finalSphereSpeed);
				loc.subtract(vector);
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}

	private void incSphereRainbow(AtomicDouble sphereHue, AtomicDouble sphereRed, AtomicDouble sphereGreen, AtomicDouble sphereBlue) {
		sphereHue.set(ParticleUtils.incHue(sphereHue.get()));
		double[] rgb = ParticleUtils.incRainbow(sphereHue.get());
		sphereRed.set(rgb[0]);
		sphereGreen.set(rgb[1]);
		sphereBlue.set(rgb[2]);
	}

	private void incLineRainbow(AtomicDouble lineHue, AtomicDouble lineRed, AtomicDouble lineGreen, AtomicDouble lineBlue) {
		lineHue.set(ParticleUtils.incHue(lineHue.get()));
		double[] rgb = ParticleUtils.incRainbow(lineHue.get());
		lineRed.set(rgb[0]);
		lineGreen.set(rgb[1]);
		lineBlue.set(rgb[2]);
	}

	public enum Direction {UP, DOWN, BOTH}

	public enum RainbowOption {SLOW, FAST, LINE}
}
