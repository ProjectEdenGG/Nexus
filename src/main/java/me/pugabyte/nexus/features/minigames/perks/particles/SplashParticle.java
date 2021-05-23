package me.pugabyte.nexus.features.minigames.perks.particles;

import me.pugabyte.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SplashParticle implements PlayerParticlePerk {
	@Override
	public @NotNull String getName() {
		return "Splash";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.BLUE_GLAZED_TERRACOTTA);
	}

	@Override
	public @NotNull String getDescription() {
		return "Summon water particles like a fish in water";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public double getSpeed() {
		return 0.01;
	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Particle getParticle() {
		return Particle.WATER_WAKE;
	}
}
