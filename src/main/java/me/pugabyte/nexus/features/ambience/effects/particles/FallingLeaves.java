package me.pugabyte.nexus.features.ambience.effects.particles;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.ambience.effects.particles.common.ParticleEffect;
import me.pugabyte.nexus.features.ambience.effects.particles.common.ParticleEffectType;
import me.pugabyte.nexus.features.particles.effects.DotEffect;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.utils.RandomUtils;
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

	public static final int LIFE = Time.SECOND.x(6);

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
				.ticks(Time.SECOND.get())
				.start();
	}

}
