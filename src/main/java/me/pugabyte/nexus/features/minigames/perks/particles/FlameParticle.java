package me.pugabyte.nexus.features.minigames.perks.particles;

import me.pugabyte.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class FlameParticle extends PlayerParticlePerk {
	@Override
	public String getName() {
		return "Flames";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.TORCH);
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
