package me.pugabyte.nexus.features.minigames.perks.particles;

import me.pugabyte.nexus.features.minigames.models.perks.common.ParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class CloudParticle extends ParticlePerk {
	@Override
	public String getName() {
		return "Clouds";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.WHITE_CONCRETE_POWDER);
	}

	@Override
	public String getDescription() {
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
}
