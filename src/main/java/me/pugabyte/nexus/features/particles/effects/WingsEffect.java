package me.pugabyte.nexus.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import eden.utils.TimeUtils.Time;
import lombok.Builder;
import lombok.Getter;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import me.pugabyte.nexus.features.particles.VectorUtils;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class WingsEffect {

	@Getter
	int taskId;
	private static final boolean o = false;
	private static final boolean x = true;

	@Builder(buildMethodName = "start")
	public WingsEffect(Player player, boolean flapMode, WingStyle wingStyle, int ticks, int flapSpeed,
					   Color color1, boolean rainbow1, Color color2, boolean rainbow2, Color color3, boolean rainbow3,
					   int startDelay, int pulseDelay) {

		if (wingStyle == null) wingStyle = WingStyle.ONE;

		boolean[][] shape1 = wingStyle.shape1;
		boolean[][] shape2 = wingStyle.shape2;
		boolean[][] shape3 = wingStyle.shape3;

		if (flapSpeed > 4)
			flapSpeed = 4;
		if (flapSpeed < 1)
			flapSpeed = 1;

		int flapRange = 20;
		if (flapMode)
			flapRange = 25;
		final float[] flap = {0.0F};
		final float[] stepFlap = {flapSpeed}; // flapSpeed

		final double space = 0.2;
		final double height = 0;
		final int wingAngle = 125;
		Particle wingParticle = Particle.REDSTONE;

		if (ticks == 0) ticks = Time.SECOND.x(5);
		if (pulseDelay < 1) pulseDelay = 2;

		if (color1 == null)
			color1 = Color.RED;
		if (color2 == null)
			color2 = Color.RED;
		if (color3 == null)
			color3 = Color.RED;

		int disX1, disY1, disZ1, disX2, disY2, disZ2, disX3, disY3, disZ3;
		disX1 = color1.getRed();
		disY1 = color1.getGreen();
		disZ1 = color1.getBlue();
		disX2 = color2.getRed();
		disY2 = color2.getGreen();
		disZ2 = color2.getBlue();
		disX3 = color3.getRed();
		disY3 = color3.getGreen();
		disZ3 = color3.getBlue();

		final AtomicDouble hue1 = new AtomicDouble(0);
		final AtomicInteger red1 = new AtomicInteger(disX1);
		final AtomicInteger green1 = new AtomicInteger(disY1);
		final AtomicInteger blue1 = new AtomicInteger(disZ1);

		final AtomicDouble hue2 = new AtomicDouble(0);
		final AtomicInteger red2 = new AtomicInteger(disX2);
		final AtomicInteger green2 = new AtomicInteger(disY2);
		final AtomicInteger blue2 = new AtomicInteger(disZ2);

		final AtomicDouble hue3 = new AtomicDouble(0);
		final AtomicInteger red3 = new AtomicInteger(disX3);
		final AtomicInteger green3 = new AtomicInteger(disY3);
		final AtomicInteger blue3 = new AtomicInteger(disZ3);

		AtomicInteger ticksElapsed = new AtomicInteger(0);
		int finalTicks = ticks;
		int finalFlapRange = flapRange;

		taskId = Tasks.repeatAsync(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				new ParticleService().get(player).cancel(taskId);
				return;
			}

			Location newLoc = player.getLocation();
			double x;
			double defX = x = newLoc.getX() + space;
			double y = newLoc.clone().getY() + 2.7D + height;
			double y2 = newLoc.clone().getY() + 2.7D + height;
			double y3 = newLoc.clone().getY() + 2.7D + height;
			Location target;
			Vector vR;
			Vector vL;
			Vector v2;
			double rightWing;
			double leftWing;

			if (rainbow1) {
				hue1.set(ParticleUtils.incHue(hue1.get()));
				int[] rgb = ParticleUtils.incRainbow(hue1.get());
				red1.set(rgb[0]);
				green1.set(rgb[1]);
				blue1.set(rgb[2]);
			}

			if (rainbow2) {
				hue2.set(ParticleUtils.incHue(hue2.get()));
				int[] rgb = ParticleUtils.incRainbow(hue2.get());
				red2.set(rgb[0]);
				green2.set(rgb[1]);
				blue2.set(rgb[2]);
			}

			if (rainbow3) {
				hue3.set(ParticleUtils.incHue(hue3.get()));
				int[] rgb = ParticleUtils.incRainbow(hue3.get());
				red3.set(rgb[0]);
				green3.set(rgb[1]);
				blue3.set(rgb[2]);
			}

			for (boolean[] booleans : shape1) {
				for (boolean bool : booleans) {
					if (bool) {
						target = newLoc.clone();
						target.setX(x);
						target.setY(y);
						vR = (target.toVector().subtract(newLoc.toVector()));
						vL = (target.toVector().subtract(newLoc.toVector()));
						v2 = (VectorUtils.getBackVector(newLoc));
						rightWing = Math.toRadians(newLoc.getYaw() + 90.0F - (wingAngle - flap[0]));
						leftWing = Math.toRadians(newLoc.getYaw() + 90.0F + (wingAngle - flap[0]));
						vR = (VectorUtils.rotateAroundAxisY(vR, -rightWing));
						vL = (VectorUtils.rotateAroundAxisY(vL, -leftWing));
						v2.setY(0).multiply(-0.2D);

						Particle.DustOptions dustOptions = ParticleUtils.newDustOption(wingParticle, red1.get(), green1.get(), blue1.get());
						ParticleUtils.display(wingParticle, newLoc.clone().add(vL).add(v2), 0, red1.get(), green1.get(), blue1.get(), 1, dustOptions);
						ParticleUtils.display(wingParticle, newLoc.clone().add(vR).add(v2), 0, red1.get(), green1.get(), blue1.get(), 1, dustOptions);
					}

					x += space;
				}

				y -= space;
				x = defX;
			}

			for (boolean[] booleans : shape2) {
				for (boolean bool : booleans) {
					if (bool) {
						target = newLoc.clone();
						target.setX(x);
						target.setY(y2);
						vR = target.toVector().subtract(newLoc.toVector());
						vL = target.toVector().subtract(newLoc.toVector());
						v2 = VectorUtils.getBackVector(newLoc);
						rightWing = Math.toRadians(newLoc.getYaw() + 90.0F - (wingAngle - flap[0]));
						leftWing = Math.toRadians(newLoc.getYaw() + 90.0F + (wingAngle - flap[0]));
						vR = VectorUtils.rotateAroundAxisY(vR, -rightWing);
						vL = VectorUtils.rotateAroundAxisY(vL, -leftWing);
						v2.setY(0).multiply(-0.2D);

						Particle.DustOptions dustOptions = ParticleUtils.newDustOption(wingParticle, red2.get(), green2.get(), blue2.get());
						ParticleUtils.display(wingParticle, newLoc.clone().add(vL).add(v2), 0, red2.get(), green2.get(), blue2.get(), 1, dustOptions);
						ParticleUtils.display(wingParticle, newLoc.clone().add(vR).add(v2), 0, red2.get(), green2.get(), blue2.get(), 1, dustOptions);
					}

					x += space;
				}

				y2 -= space;
				x = defX;
			}

			for (boolean[] booleans : shape3) {
				for (boolean aBoolean : booleans) {
					if (aBoolean) {
						target = newLoc.clone();
						target.setX(x);
						target.setY(y3);
						vR = target.toVector().subtract(newLoc.toVector());
						vL = target.toVector().subtract(newLoc.toVector());
						v2 = VectorUtils.getBackVector(newLoc);
						rightWing = Math.toRadians(newLoc.getYaw() + 90.0F - (wingAngle - flap[0]));
						leftWing = Math.toRadians(newLoc.getYaw() + 90.0F + (wingAngle - flap[0]));
						vR = VectorUtils.rotateAroundAxisY(vR, -rightWing);
						vL = VectorUtils.rotateAroundAxisY(vL, -leftWing);
						v2.setY(0).multiply(-0.2D);

						Particle.DustOptions dustOptions = ParticleUtils.newDustOption(wingParticle, red3.get(), green3.get(), blue3.get());
						ParticleUtils.display(wingParticle, newLoc.clone().add(vL).add(v2), 0, red3.get(), green3.get(), blue3.get(), 1, dustOptions);
						ParticleUtils.display(wingParticle, newLoc.clone().add(vR).add(v2), 0, red3.get(), green3.get(), blue3.get(), 1, dustOptions);
					}
					x += space;
				}
				y3 -= space;
				x = defX;
			}

			if (flapMode) {
				if (flap[0] > finalFlapRange)
					stepFlap[0] = -stepFlap[0];
				else if (flap[0] < 0.0F)
					stepFlap[0] = -stepFlap[0];

				flap[0] += stepFlap[0];
			}

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}

	@Getter
	public enum WingStyle {
		ONE(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o}}
		),
		TWO(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		THREE(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, x, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, x, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, x, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, x, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, x, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, x, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, x, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		FOUR(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, x, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, x, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, x, o, o, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, x, o, o, o, o, o, o, o, o},
						{o, o, x, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		FIVE(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		SIX(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, x, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, x, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, x, o, o, o, o, o, o},
						{o, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, x, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		SEVEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, x, o, o, o, o, o},
						{o, o, o, o, o, o, o, x, x, o, o, o, o, o},
						{o, o, o, o, o, o, x, x, o, o, o, o, o, o},
						{o, o, o, o, o, x, x, x, o, o, o, o, o, o},
						{o, o, o, x, x, x, x, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, x, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, x, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		EIGHT(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, x, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, x, o, o, o, o, o, o, o},
						{o, x, o, o, x, o, x, o, o, o, o, o, o, o},
						{x, o, o, x, o, o, x, o, o, o, o, o, o, o},
						{x, o, x, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, x, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, x, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, x, o, o, o, o, o, o, o, o},
						{o, x, o, o, x, x, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, x, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		NINE(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		TEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		ELEVEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, o, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		TWELVE(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, x, o, x, x, o, o, o, o, o, o, o},
						{x, o, o, x, x, o, x, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, x, x, x, o, x, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, x, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, o, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		THIRTEEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, x, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, x, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, o, x, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, x, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, x, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, x, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, x, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, x, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, x, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, x, x, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, x, x, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, x, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		FOURTEEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, o, o, x, o, o, o, o, o},
						{x, o, o, o, o, o, x, x, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		FIFTEEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, x, o, o, o, o, o, o, o, o, o},
						{x, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, x, o, o, o, o, o, o, o, o, o, o},
						{x, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, x, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, x, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, x, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		SIXTEEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, o, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, x, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{x, o, o, o, o, o, x, o, o, o, o, o, o, o},
						{x, o, x, o, x, o, x, o, o, o, o, o, o, o},
						{o, x, o, x, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, o, o, o, o, o, o, o, o, o, o, o},
						{x, x, x, x, o, o, o, o, o, o, o, o, o, o},
						{x, x, o, x, x, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, x, o, o, o, o, o, o, o, o, o, o, o},
						{o, x, x, x, x, o, o, o, o, o, o, o, o, o},
						{o, x, o, x, o, x, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		);

		boolean[][] shape1;
		boolean[][] shape2;
		boolean[][] shape3;

		WingStyle(boolean[][] shape1, boolean[][] shape2, boolean[][] shape3) {
			this.shape1 = shape1;
			this.shape2 = shape2;
			this.shape3 = shape3;
		}
	}
}
