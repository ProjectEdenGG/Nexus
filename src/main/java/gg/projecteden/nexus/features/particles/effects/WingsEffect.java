package gg.projecteden.nexus.features.particles.effects;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.ParticleUtils;
import gg.projecteden.nexus.features.particles.ParticleUtils.ParticleColor;
import gg.projecteden.nexus.features.particles.VectorUtils;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleSetting;
import gg.projecteden.nexus.models.particle.ParticleTask;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WingsEffect {

	@Getter
	int taskId;
	private static final boolean o = false;
	private static final boolean x = true;

	@Builder(buildMethodName = "start")
	public WingsEffect(ParticleOwner owner, HumanEntity entity, boolean flapMode, WingStyle wingStyle, long ticks, int flapSpeed,
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

		if (ticks == 0)
			ticks = TickTime.SECOND.x(5);
		if (pulseDelay < 1)
			pulseDelay = 2;

		final ParticleColor _color1 = new ParticleColor(color1 == null ? Color.RED : color1);
		final ParticleColor _color2 = new ParticleColor(color2 == null ? Color.RED : color2);
		final ParticleColor _color3 = new ParticleColor(color3 == null ? Color.RED : color3);

		final AtomicInteger ticksElapsed = new AtomicInteger(0);
		long finalTicks = ticks;
		int finalFlapRange = flapRange;

		final Player receiver = PlayerUtils.isSelf(owner, entity) ? null : owner == null ? null : owner.getOnlinePlayer();

		taskId = Tasks.repeatAsync(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks) {
				if (owner == null)
					Tasks.cancel(taskId);
				else
					owner.cancel(taskId);
				return;
			}

			if (rainbow1)
				_color1.incrementRainbow();

			if (rainbow2)
				_color2.incrementRainbow();

			if (rainbow3)
				_color3.incrementRainbow();

			Location newLoc = entity.getLocation();
			double x;
			double defX = x = newLoc.getX() + space;
			double y = newLoc.clone().getY() + 2.7D + height;
			double y2 = newLoc.clone().getY() + 2.7D + height;
			double y3 = newLoc.clone().getY() + 2.7D + height;

			for (boolean[] pixels : shape1) {
				for (boolean pixel : pixels) {
					if (pixel)
						display(receiver, flap, wingAngle, wingParticle, _color1, newLoc, x, y);
					x += space;
				}
				y -= space;
				x = defX;
			}

			for (boolean[] pixels : shape2) {
				for (boolean pixel : pixels) {
					if (pixel)
						display(receiver, flap, wingAngle, wingParticle, _color2, newLoc, x, y2);
					x += space;
				}
				y2 -= space;
				x = defX;
			}

			for (boolean[] pixels : shape3) {
				for (boolean pixel : pixels) {
					if (pixel)
						display(receiver, flap, wingAngle, wingParticle, _color3, newLoc, x, y3);
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

	private void display(Player receiver, float[] flap, int wingAngle, Particle wingParticle, ParticleColor color, Location newLoc, double x, double y2) {
		Location target;
		Vector vR, vL, v2;
		double rightWing, leftWing;

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

		Particle.DustOptions dustOptions = ParticleUtils.newDustOption(wingParticle, color);
		ParticleUtils.display(receiver, wingParticle, newLoc.clone().add(vL).add(v2), 0, color.getRed(), color.getGreen(), color.getBlue(), 1, dustOptions);
		ParticleUtils.display(receiver, wingParticle, newLoc.clone().add(vR).add(v2), 0, color.getRed(), color.getGreen(), color.getBlue(), 1, dustOptions);
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

		private static final List<String> wordToIndex = new ArrayList<>(Arrays.asList("zero", "one", "two", "three", "four", "five",
				"six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen"));

		public boolean canBeUsedBy(Player player) {
			return player.hasPermission(getPermission());
		}

		public String getPermission() {
			return "wings.style." + wordToIndex.indexOf(name().toLowerCase());
		}

		public ItemBuilder getDisplayItem() {
			return new ItemBuilder(Material.ELYTRA).name("&3Style #" + (ordinal() + 1));
		}

		public void preview(HumanEntity entity) {
			ParticleOwner owner = new ParticleService().get(entity);
			owner.cancel(ParticleType.WINGS);

			Map<ParticleSetting, Object> wingSettings = owner.getSettings(ParticleType.WINGS);
			WingsEffect.WingStyle cur_Style = (WingsEffect.WingStyle) wingSettings.get(ParticleSetting.WINGS_STYLE);
			Color cur_Color1 = (Color) wingSettings.get(ParticleSetting.WINGS_COLOR_ONE);
			Color cur_Color2 = (Color) wingSettings.get(ParticleSetting.WINGS_COLOR_TWO);
			Color cur_Color3 = (Color) wingSettings.get(ParticleSetting.WINGS_COLOR_THREE);
			Boolean cur_Rainbow1 = (Boolean) wingSettings.get(ParticleSetting.WINGS_RAINBOW_ONE);
			Boolean cur_Rainbow2 = (Boolean) wingSettings.get(ParticleSetting.WINGS_RAINBOW_TWO);
			Boolean cur_Rainbow3 = (Boolean) wingSettings.get(ParticleSetting.WINGS_RAINBOW_THREE);

			// Default Preview Settings
			wingSettings.put(ParticleSetting.WINGS_STYLE, this);
			wingSettings.put(ParticleSetting.WINGS_COLOR_ONE, ColorType.YELLOW.getBukkitColor());
			wingSettings.put(ParticleSetting.WINGS_COLOR_TWO, ColorType.ORANGE.getBukkitColor());
			wingSettings.put(ParticleSetting.WINGS_COLOR_THREE, ColorType.CYAN.getBukkitColor());
			wingSettings.put(ParticleSetting.WINGS_RAINBOW_ONE, false);
			wingSettings.put(ParticleSetting.WINGS_RAINBOW_TWO, false);
			wingSettings.put(ParticleSetting.WINGS_RAINBOW_THREE, false);

			Tasks.wait(5, () -> {
				ParticleType.WINGS.run(owner);

				owner.getTasks().add(new ParticleTask(ParticleType.WINGS, Tasks.wait(TickTime.SECOND.x(15), () -> {
					owner.cancel(ParticleType.WINGS);
					wingSettings.put(ParticleSetting.WINGS_STYLE, cur_Style);
					wingSettings.put(ParticleSetting.WINGS_COLOR_ONE, cur_Color1);
					wingSettings.put(ParticleSetting.WINGS_COLOR_TWO, cur_Color2);
					wingSettings.put(ParticleSetting.WINGS_COLOR_THREE, cur_Color3);
					wingSettings.put(ParticleSetting.WINGS_RAINBOW_ONE, cur_Rainbow1);
					wingSettings.put(ParticleSetting.WINGS_RAINBOW_TWO, cur_Rainbow2);
					wingSettings.put(ParticleSetting.WINGS_RAINBOW_THREE, cur_Rainbow3);
				})));
			});
		}
	}
}
