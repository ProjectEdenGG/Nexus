package me.pugabyte.nexus.features.minigames.perks.particles;

import me.pugabyte.nexus.features.minigames.models.perks.common.ParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class SoulFlameParticle extends ParticlePerk {
	@Override
	public String getName() {
		return "Soul Flames";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.SOUL_TORCH);
	}

	@Override
	public String getDescription() {
		return "Burn like the flames of the nether wastes";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public Particle getParticle() {
		return Particle.SOUL_FIRE_FLAME;
	}

	@Override
	public double getSpeed() {
		return 0.002d;
	}
}
