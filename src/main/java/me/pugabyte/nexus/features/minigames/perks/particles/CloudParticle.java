package me.pugabyte.nexus.features.minigames.perks.particles;

import me.pugabyte.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CloudParticle extends PlayerParticlePerk {
	@Override
	public String getName() {
		return "Clouds";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.WHITE_CONCRETE_POWDER);
	}

	@Override
	public @NotNull String getDescription() {
		return "Hide yourself in a cloud of smoke";
	}

	@Override
	public int getPrice() {
		return 15;
	}

	@Override
	public Particle getParticle() {
		return Particle.CLOUD;
	}

	@Override
	public double getSpeed() {
		return 0.01;
	}
}
