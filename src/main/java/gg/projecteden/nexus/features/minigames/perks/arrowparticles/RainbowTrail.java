package gg.projecteden.nexus.features.minigames.perks.arrowparticles;

import gg.projecteden.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import gg.projecteden.nexus.features.particles.ParticleUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class RainbowTrail implements ParticleProjectilePerk {
	@Override
	public @NotNull String getName() {
		return "Rainbow Trail";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.RED_GLAZED_TERRACOTTA);
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Turn your arrows multi-colored with rainbow trails");
	}

	@Override
	public int getPrice() {
		return 50;
	}

	@Override
	public Particle getParticle() {
		return Particle.DUST;
	}

	@Override
	public double getSpeed() {
		return 0.01;
	}

	@Override
	public Particle.DustOptions getDustOptions(@NotNull Projectile projectile) {
		return ParticleUtils.newDustOption(getParticle(), ParticleUtils.incRainbow(projectile.getTicksLived()));
	}
}
