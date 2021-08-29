package gg.projecteden.nexus.features.ambience.effects.particles;

import gg.projecteden.nexus.features.ambience.effects.particles.common.ParticleEffect;
import gg.projecteden.nexus.features.ambience.effects.particles.common.ParticleEffectType;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class Fireflies extends ParticleEffect {
	private double x;
	private double y;
	private double z;
	private static final int RANGE = 5;

	public static final int LIFE = TickTime.SECOND.x(10);

	public Fireflies(AmbienceUser user, Block block, double chance) {
		super(user, ParticleEffectType.FIREFLIES, Particle.END_ROD, LIFE, chance);

		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
	}

	@Override
	public void play() {
		Player player = user.getPlayer();
		if (player == null)
			return;

		double xRange = x + (Math.random() * RANGE + 3) - RANGE;
		double yRange = y + (Math.random() * RANGE);
		double zRange = z + (Math.random() * RANGE + 3) - RANGE;
		double xVel = 0.5 * (Math.random() - 0.5);
		double yVel = 0.2 * (Math.random() - 0.5);
		double zVel = 0.5 * (Math.random() - 0.5);

		player.spawnParticle(particle, xRange, yRange, zRange, 0, xVel, yVel, zVel, 1);

		if (user.isDebug())
			DotEffect.builder()
				.player(player)
				.location(new Location(player.getWorld(), xRange, yRange, zRange))
				.clientSide(true)
				.color(Color.YELLOW)
				.speed(.1)
				.ticks(TickTime.SECOND.get())
				.start();
	}

}
