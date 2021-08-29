package gg.projecteden.nexus.features.ambience.effects.particles;

import gg.projecteden.nexus.features.ambience.effects.particles.common.ParticleEffect;
import gg.projecteden.nexus.features.ambience.effects.particles.common.ParticleEffectType;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class FallingLeaves extends ParticleEffect {
	private Material material;
	private double x;
	private double y;
	private double z;

	public static final int LIFE = TickTime.SECOND.x(6);

	public FallingLeaves(AmbienceUser user, Block block, double chance) {
		super(user, ParticleEffectType.FALLING_LEAVES, Particle.BLOCK_CRACK, LIFE, chance);

		this.material = block.getType();
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
	}

	@Override
	public void play() {
		Player player = user.getPlayer();
		if (player == null)
			return;

		double xRange = x + RandomUtils.randomDouble(.2, .8);
		double yRange = y - 0.05;
		double zRange = z + RandomUtils.randomDouble(.2, .8);

		player.spawnParticle(particle, xRange, yRange, zRange, 0, 0, 0, 0, 1, Bukkit.createBlockData(material));

		if (user.isDebug())
			DotEffect.builder()
				.player(player)
				.location(new Location(player.getWorld(), xRange, yRange, zRange))
				.clientSide(true)
				.color(Color.RED)
				.speed(.1)
				.ticks(TickTime.SECOND.get())
				.start();
	}

}
