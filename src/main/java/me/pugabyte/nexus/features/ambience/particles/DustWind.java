package me.pugabyte.nexus.features.ambience.particles;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.ambience.Wind;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffect;
import me.pugabyte.nexus.features.ambience.particles.common.ParticleEffectType;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class DustWind extends ParticleEffect {
	private Material material;
	private double x;
	private double y;
	private double z;
	private double windX;
	private double windZ;

	private static final int LIFE = Time.SECOND.x(3);

	public DustWind(AmbienceUser user, Material material, double x, double y, double z, double chance) {
		super(user, ParticleEffectType.DUST_WIND, Particle.ITEM_CRACK, LIFE, chance);

		this.material = material;
		this.x = x;
		this.y = y;
		this.z = z;
		this.windX = Wind.getX();
		this.windZ = Wind.getZ();
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

}
