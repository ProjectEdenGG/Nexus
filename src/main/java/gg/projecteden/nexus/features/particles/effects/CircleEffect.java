package gg.projecteden.nexus.features.particles.effects;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.ParticleUtils;
import gg.projecteden.nexus.features.particles.VectorUtils;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class CircleEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public CircleEffect(HumanEntity player, Location location, boolean updateLoc, Vector updateVector, Particle particle, boolean whole, boolean randomRotation,
						boolean rainbow, Color color, int count, int density, long ticks, double radius, double speed, boolean fast,
						double disX, double disY, double disZ, int startDelay, int pulseDelay, boolean clientSide) {

		if (player != null && location == null)
			location = player.getLocation();

		if (density == 0) density = 20;
		double inc = (2 * Math.PI) / density;
		int steps = whole ? density : 1;
		int loops = 1;
		if (fast) loops = 10;
		if (updateVector != null) updateLoc = true;
		if (updateVector == null && updateLoc) updateVector = new Vector(0, 0, 0);

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

		double angularVelocityX = Math.PI / 200;
		double angularVelocityY = Math.PI / 170;
		double angularVelocityZ = Math.PI / 155;

		double finalSpeed = speed;
		int finalCount = count;
		long finalTicks = ticks;
		Particle finalParticle = particle;
		int finalLoops = loops;
		Location finalLocation = location;
		boolean finalUpdateLoc = updateLoc;
		Vector finalUpdateVector = updateVector;
		final AtomicDouble hue = new AtomicDouble(0);
		final AtomicInteger red = new AtomicInteger((int) disX);
		final AtomicInteger green = new AtomicInteger((int) disY);
		final AtomicInteger blue = new AtomicInteger((int) disZ);
		AtomicInteger ticksElapsed = new AtomicInteger(0);
		AtomicInteger step = new AtomicInteger(0);

		taskId = Tasks.repeat(startDelay, pulseDelay, () -> {
			if (finalTicks != -1 && ticksElapsed.get() >= finalTicks && player != null) {
				new ParticleService().get(player).cancel(taskId);
				return;
			}

			drawCircle(player, randomRotation, rainbow, radius, inc, steps, angularVelocityX, angularVelocityY, angularVelocityZ, finalSpeed, finalCount, finalParticle, finalLoops, finalLocation, finalUpdateLoc, finalUpdateVector, hue, red, green, blue, step, clientSide);

			if (finalTicks != -1)
				ticksElapsed.incrementAndGet();
		});
	}

	private static void drawCircle(HumanEntity player, boolean randomRotation, boolean rainbow, double radius, double inc, int steps, double angularVelocityX, double angularVelocityY, double angularVelocityZ, double finalSpeed, int finalCount, Particle finalParticle, int finalLoops, Location finalLocation, boolean finalUpdateLoc, Vector finalUpdateVector, AtomicDouble hue, AtomicInteger red, AtomicInteger green, AtomicInteger blue, AtomicInteger step, boolean clientSide) {
		for (int j = 0; j < finalLoops; j++) {
			if (rainbow) {
				hue.set(ParticleUtils.incHue(hue.get()));
				int[] rgb = ParticleUtils.incRainbow(hue.get());
				red.set(rgb[0]);
				green.set(rgb[1]);
				blue.set(rgb[2]);
			}

			Location loc = finalLocation;
			if (finalUpdateLoc && player != null)
				loc = player.getLocation().add(finalUpdateVector);

			for (int i = 0; i < steps; i++) {
				double angle = step.get() * inc;
				Vector v = new Vector();
				v.setX(Math.cos(angle) * radius);
				v.setZ(Math.sin(angle) * radius);
				if (randomRotation)
					VectorUtils.rotateVector(v, angularVelocityX * step.get(), angularVelocityY * step.get(), angularVelocityZ * step.get());

				Particle.DustOptions dustOptions = ParticleUtils.newDustOption(finalParticle, red.get(), green.get(), blue.get());
				display(player, finalSpeed, finalCount, finalParticle, red, green, blue, clientSide, loc, v, dustOptions);

				step.getAndIncrement();
			}
		}
	}

	private static void display(HumanEntity player, double finalSpeed, int finalCount, Particle finalParticle, AtomicInteger red, AtomicInteger green, AtomicInteger blue, boolean clientSide, Location loc, Vector v, DustOptions dustOptions) {
		if (clientSide && player != null) {
			if (player instanceof Player)
				ParticleUtils.display((Player) player, finalParticle, loc.clone().add(v), finalCount, red.get(), green.get(), blue.get(), finalSpeed, dustOptions);
		} else
			ParticleUtils.display(finalParticle, loc.clone().add(v), finalCount, red.get(), green.get(), blue.get(), finalSpeed, dustOptions);
	}
}
