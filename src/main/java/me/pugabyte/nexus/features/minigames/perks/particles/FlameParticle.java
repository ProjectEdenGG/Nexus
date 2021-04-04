package me.pugabyte.nexus.features.minigames.perks.particles;

import me.pugabyte.nexus.features.minigames.models.perks.common.ParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class FlameParticle extends ParticlePerk {
	@Override
	public String getName() {
		return "Flame";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.MAGMA_BLOCK);
	}

	@Override
	public String getDescription() {
		return "Burn like the fire that fuels your craving for blood!";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public Particle getParticle() {
		return Particle.FLAME;
	}

	@Override
	public double getSpeed() {
		return 0.002d;
	}
}
