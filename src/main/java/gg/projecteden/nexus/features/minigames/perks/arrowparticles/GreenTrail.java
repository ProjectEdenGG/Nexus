package gg.projecteden.nexus.features.minigames.perks.arrowparticles;

import gg.projecteden.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GreenTrail implements ParticleProjectilePerk {
	@Override
	public @NotNull String getName() {
		return "Green Trail";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.LIME_DYE);
	}

	@Override
	public @NotNull String getDescription() {
		return "A brief green trail for your arrows";
	}

	@Override
	public int getPrice() {
		return 20;
	}

	@Override
	public Particle getParticle() {
		return Particle.COMPOSTER;
	}

	@Override
	public double getSpeed() {
		return 0.001;
	}
}
