package me.pugabyte.nexus.features.ambience.particles;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffect;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffectInstance;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffects.AmbienceEffect;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class FallingLeaves extends ParticleEffectInstance {
	private Material material;
	private double x;
	private double y;
	private double z;

	public FallingLeaves(AmbienceUser user, Material material, double x, double y, double z, double chance) {
		super(user, AmbienceEffect.FALLING_LEAVES, Particle.BLOCK_CRACK, Time.SECOND.x(6), chance);

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

		double xRange = x + Math.random();
		double yRange = y - 0.55;
		double zRange = z + Math.random();

		player.spawnParticle(getParticle(), xRange, yRange, zRange, 0, 0, 0, 0, 1, Bukkit.createBlockData(material));
	}

	public static boolean conditionsMet(ParticleEffect particleEffect, AmbienceUser user, Block block, double x, double y, double z) {
		Player player = user.getPlayer();
		if (player == null)
			return false;

		if (!particleEffect.getSpawnMaterial().equals(block.getType()) || !particleEffect.getBelowMaterial().equals(block.getRelative(0, 1, 0).getType()))
			return false;

		return true;
	}
}
