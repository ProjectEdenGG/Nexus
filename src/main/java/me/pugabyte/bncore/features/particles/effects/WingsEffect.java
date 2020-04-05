package me.pugabyte.bncore.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Builder;
import lombok.Getter;
import me.pugabyte.bncore.features.particles.ParticleUtils;
import me.pugabyte.bncore.features.particles.VectorUtils;
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

public class WingsEffect {

	@Getter
	int taskId;
	private static boolean o = false;
	private static boolean _ = true;

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
		if (pulseDelay < 1) pulseDelay = 1;

		if (color1 == null)
			color1 = Color.RED;
		if (color2 == null)
			color2 = Color.RED;
		if (color3 == null)
			color3 = Color.RED;

		double disX1, disY1, disZ1, disX2, disY2, disZ2, disX3, disY3, disZ3;
		disX1 = color1.getRed();
		disY1 = color1.getGreen();
		disZ1 = color1.getBlue();
		disX2 = color2.getRed();
		disY2 = color2.getGreen();
		disZ2 = color2.getBlue();
		disX3 = color3.getRed();
		disY3 = color3.getGreen();
		disZ3 = color3.getBlue();

		if (!rainbow1) {
			disX1 /= 255.0;
			disY1 /= 255.0;
			disZ1 /= 255.0;
			disX1 = disX1 == 0.0 ? 0.001 : disX1;
		}

		if (!rainbow2) {
			disX2 /= 255.0;
			disY2 /= 255.0;
			disZ2 /= 255.0;
			disX2 = disX2 == 0.0 ? 0.001 : disX2;
		}

		if (!rainbow3) {
			disX3 /= 255.0;
			disY3 /= 255.0;
			disZ3 /= 255.0;
			disX3 = disX3 == 0.0 ? 0.001 : disX3;
		}

		final AtomicDouble hue1 = new AtomicDouble(0);
		final AtomicDouble red1 = new AtomicDouble(disX1);
		final AtomicDouble green1 = new AtomicDouble(disY1);
		final AtomicDouble blue1 = new AtomicDouble(disZ1);

		final AtomicDouble hue2 = new AtomicDouble(0);
		final AtomicDouble red2 = new AtomicDouble(disX2);
		final AtomicDouble green2 = new AtomicDouble(disY2);
		final AtomicDouble blue2 = new AtomicDouble(disZ2);

		final AtomicDouble hue3 = new AtomicDouble(0);
		final AtomicDouble red3 = new AtomicDouble(disX3);
		final AtomicDouble green3 = new AtomicDouble(disY3);
		final AtomicDouble blue3 = new AtomicDouble(disZ3);

		AtomicInteger ticksElapsed = new AtomicInteger(0);
		int finalTicks = ticks;
		int finalFlapRange = flapRange;

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				((ParticleOwner) new ParticleService().get(player)).cancelTasks(taskId);
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
				double[] rgb = ParticleUtils.incRainbow(hue1.get());
				red1.set(rgb[0]);
				green1.set(rgb[1]);
				blue1.set(rgb[2]);
			}

			if (rainbow2) {
				hue2.set(ParticleUtils.incHue(hue2.get()));
				double[] rgb = ParticleUtils.incRainbow(hue2.get());
				red2.set(rgb[0]);
				green2.set(rgb[1]);
				blue2.set(rgb[2]);
			}

			if (rainbow3) {
				hue3.set(ParticleUtils.incHue(hue3.get()));
				double[] rgb = ParticleUtils.incRainbow(hue3.get());
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
						ParticleUtils.display(wingParticle, newLoc.clone().add(vL).add(v2), 0, red1.get(), green1.get(), blue1.get(), 1);
						ParticleUtils.display(wingParticle, newLoc.clone().add(vR).add(v2), 0, red1.get(), green1.get(), blue1.get(), 1);
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
						vR = (target.toVector().subtract(newLoc.toVector()));
						vL = (target.toVector().subtract(newLoc.toVector()));
						v2 = (VectorUtils.getBackVector(newLoc));
						rightWing = Math.toRadians(newLoc.getYaw() + 90.0F - (wingAngle - flap[0]));
						leftWing = Math.toRadians(newLoc.getYaw() + 90.0F + (wingAngle - flap[0]));
						vR = (VectorUtils.rotateAroundAxisY(vR, -rightWing));
						vL = (VectorUtils.rotateAroundAxisY(vL, -leftWing));
						v2.setY(0).multiply(-0.2D);
						ParticleUtils.display(wingParticle, newLoc.clone().add(vL).add(v2), 0, red2.get(), green2.get(), blue2.get(), 1);
						ParticleUtils.display(wingParticle, newLoc.clone().add(vR).add(v2), 0, red2.get(), green2.get(), blue2.get(), 1);
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
						vR = (target.toVector().subtract(newLoc.toVector()));
						vL = (target.toVector().subtract(newLoc.toVector()));
						v2 = (VectorUtils.getBackVector(newLoc));
						rightWing = Math.toRadians(newLoc.getYaw() + 90.0F - (wingAngle - flap[0]));
						leftWing = Math.toRadians(newLoc.getYaw() + 90.0F + (wingAngle - flap[0]));
						vR = (VectorUtils.rotateAroundAxisY(vR, -rightWing));
						vL = (VectorUtils.rotateAroundAxisY(vL, -leftWing));
						v2.setY(0).multiply(-0.2D);
						ParticleUtils.display(wingParticle, newLoc.clone().add(vL).add(v2), 0, red3.get(), green3.get(), blue3.get(), 1);
						ParticleUtils.display(wingParticle, newLoc.clone().add(vR).add(v2), 0, red3.get(), green3.get(), blue3.get(), 1);
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
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, _, o, o, o, o, o, o, o, o, o, o, o},
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
						{_, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o}}
		),
		TWO(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		THREE(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, _, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, _, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, _, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, _, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, _, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, _, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, _, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		FOUR(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, _, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, _, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, _, o, o, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, _, o, o, o, o, o, o, o, o},
						{o, o, _, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		FIVE(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, _, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, _, _, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, _, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, _, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, _, o, o, o, o, o, o},
						{o, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, _, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, _, o, o, o, o, o},
						{o, o, o, o, o, o, o, _, _, o, o, o, o, o},
						{o, o, o, o, o, o, _, _, o, o, o, o, o, o},
						{o, o, o, o, o, _, _, _, o, o, o, o, o, o},
						{o, o, o, _, _, _, _, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, o, o, _, _, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, _, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, _, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		EIGHT(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, _, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, _, o, o, o, o, o, o, o},
						{o, _, o, o, _, o, _, o, o, o, o, o, o, o},
						{_, o, o, _, o, o, _, o, o, o, o, o, o, o},
						{_, o, _, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, _, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, _, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, _, o, o, o, o, o, o, o, o},
						{o, _, o, o, _, _, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, _, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		NINE(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, _, o, o, o, o, o, o, o, o, o},
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
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		TEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
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
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{_, _, _, o, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
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
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, _, o, _, _, o, o, o, o, o, o, o},
						{_, o, o, _, _, o, _, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, _, _, _, o, _, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, _, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{_, _, _, o, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}}
		),
		THIRTEEN(
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, _, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, _, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, o, _, o, o, o, o, o, o},
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
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, _, _, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, _, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, _, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, _, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, _, o, o, o, o},
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
						{o, o, o, _, _, _, o, o, o, o, o, o, o, o},
						{o, o, _, _, _, _, _, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, _, _, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, _, o, o, o, o, o},
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
						{o, o, _, _, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, o, o, _, o, o, o, o, o},
						{_, o, o, o, o, o, _, _, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, _, o, o, o, o, o, o, o, o, o},
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
						{o, _, _, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, _, o, o, o, o, o, o, o, o, o},
						{_, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, _, o, o, o, o, o, o, o, o, o, o},
						{_, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, _, _, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, _, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, _, o, o, o, o, o, o, o, o, o, o},
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
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, o, o, o, o, o, o, o, o, o, o, o, o},
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
						{_, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, o, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, _, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
						{_, o, o, o, o, o, _, o, o, o, o, o, o, o},
						{_, o, _, o, _, o, _, o, o, o, o, o, o, o},
						{o, _, o, _, o, _, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o}},
				new boolean[][] {
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, o, o, o, o, o, o, o, o, o, o, o},
						{_, _, _, _, o, o, o, o, o, o, o, o, o, o},
						{_, _, o, _, _, o, o, o, o, o, o, o, o, o},
						{o, o, o, o, o, _, o, o, o, o, o, o, o, o},
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
						{o, o, _, o, o, o, o, o, o, o, o, o, o, o},
						{o, _, _, _, _, o, o, o, o, o, o, o, o, o},
						{o, _, o, _, o, _, o, o, o, o, o, o, o, o},
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
