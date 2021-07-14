package me.pugabyte.nexus.features.ambience.particles;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffect;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffectInstance;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffects.AmbienceEffect;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class DustWind extends ParticleEffectInstance {
	private Material material;
	private double x;
	private double y;
	private double z;
	private double windX;
	private double windZ;

	public DustWind(AmbienceUser user, Material material, double windX, double windZ, double x, double y, double z, double chance) {
		super(user, AmbienceEffect.DUST_WIND, Particle.ITEM_CRACK, Time.SECOND.x(3), chance);

		this.material = material;
		this.x = x;
		this.y = y;
		this.z = z;
		this.windX = windX;
		this.windZ = windZ;
	}

	@Override
	public void play() {
		Player player = getUser().getPlayer();
		if (player == null)
			return;

		double xRange = x - 2 + Math.random() * 5;
		double yRange = y + 1 + Math.random() * 2;
		double zRange = z - 2 + Math.random() * 5;

		double scale = 1 + Math.random() * 0.2;
		double xVel = windX * scale;
		double yVel = 0;
		double zVel = windZ * scale;

		player.spawnParticle(getParticle(), xRange, yRange, zRange, 0, xVel, yVel, zVel, 1, new ItemStack(material));
	}

	public static boolean conditionsMet(ParticleEffect particleEffect, AmbienceUser user, Block block, double x, double y, double z) {
		Player player = user.getPlayer();
		if (player == null)
			return false;

		if (!particleEffect.getSpawnMaterial().equals(block.getType()) || !particleEffect.getAboveMaterial().equals(block.getRelative(0, 1, 0).getType()))
			return false;

		return true;

//		if(!RandomUtils.chanceOf(getChance())) return false;
//
//		Location location = player.getLocation();
//		Biome biome = location.getBlock().getBiome();
//
//		if(!Ambience.isWindBlowing()) return false;
//		if(!location.add(0, 1, 0).getBlock().getType().equals(Material.AIR)) return false;
//		if(!user.getVariables().isExposed()) return false;
//
//		return BiomeTag.ALL_DESERT.isTagged(biome) || BiomeTag.MESA.isTagged(biome);
	}
}
