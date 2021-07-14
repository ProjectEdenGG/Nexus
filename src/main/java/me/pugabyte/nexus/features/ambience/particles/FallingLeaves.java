package me.pugabyte.nexus.features.ambience.particles;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffect;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffectType;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class FallingLeaves extends ParticleEffect {
	private Material material;
	private double x;
	private double y;
	private double z;

	public static final int LIFE = Time.SECOND.x(6);

	public FallingLeaves(AmbienceUser user, Material material, double x, double y, double z, double chance) {
		super(user, ParticleEffectType.FALLING_LEAVES, Particle.BLOCK_CRACK, LIFE, chance);

		this.material = material;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void play() {
		Player player = getUser().getPlayer();
		if (player == null)
			return;

		double xRange = x + Math.random() + .5;
		double yRange = y - 0.55;
		double zRange = z + Math.random() + .5;

		player.spawnParticle(getParticle(), xRange, yRange, zRange, 0, 0, 0, 0, 1, Bukkit.createBlockData(material));
	}

}
