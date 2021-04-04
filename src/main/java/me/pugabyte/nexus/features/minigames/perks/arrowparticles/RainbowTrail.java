package me.pugabyte.nexus.features.minigames.perks.arrowparticles;

import me.pugabyte.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RainbowTrail extends ParticleProjectilePerk {
	@Override
	public String getName() {
		return "Rainbow Trail";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.RED_GLAZED_TERRACOTTA);
	}

	@Override
	public String getDescription() {
		return "Turn your arrows multi-colored with rainbow trails";
	}

	@Override
	public int getPrice() {
		return 50;
	}

	@Override
	public Particle getParticle() {
		return Particle.REDSTONE;
	}

	@Override
	public double getSpeed() {
		return 0.01;
	}

	@Override
	public void tick(Projectile projectile, List<Player> players) {
		int[] rgb = ParticleUtils.incRainbow(projectile.getTicksLived());
		tick(projectile, players, ParticleUtils.newDustOption(getParticle(), rgb[0], rgb[1], rgb[2]));
	}
}
