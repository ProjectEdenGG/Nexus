package me.pugabyte.nexus.features.ambience.particles;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffect;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffectInstance;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffects.AmbienceEffect;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.models.ambience.Variables.TimeQuadrant;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class Fireflies extends ParticleEffectInstance {
	private double x;
	private double y;
	private double z;
	private int range;

	public Fireflies(AmbienceUser user, double x, double y, double z, int range, double chance) {
		super(user, AmbienceEffect.FIREFLIES, Particle.END_ROD, Time.SECOND.x(10), chance);

		this.x = x;
		this.y = y;
		this.z = z;
		this.range = range;
	}

	@Override
	public void play() {
		Player player = getUser().getPlayer();
		if (player == null)
			return;

		double xRange = x + (Math.random() * range + 3) - range;
		double yRange = y + (Math.random() * range) * range;
		double zRange = z + (Math.random() * range + 3) - range;
		double xVel = 0.5 * (Math.random() - 0.5);
		double yVel = 0.2 * (Math.random() - 0.5);
		double zVel = 0.5 * (Math.random() - 0.5);

		player.spawnParticle(getParticle(), xRange, yRange, zRange, 0, xVel, yVel, zVel, 1);
	}

	public static boolean conditionsMet(ParticleEffect particleEffect, AmbienceUser user, Block block, double x, double y, double z) {
		Player player = user.getPlayer();
		if (player == null)
			return false;

		if (!particleEffect.getSpawnMaterial().equals(block.getType()) || !particleEffect.getAboveMaterial().equals(block.getRelative(0, 1, 0).getType()))
			return false;

		if (!TimeQuadrant.NIGHT.equals(user.getVariables().getTimeQuadrant()))
			return false;

		return true;
	}
}
