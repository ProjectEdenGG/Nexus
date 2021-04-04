package me.pugabyte.nexus.features.minigames.perks.arrowparticles;

import me.pugabyte.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class GreenTrail extends ParticleProjectilePerk {
	@Override
	public String getName() {
		return "Green Trail";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.LIME_DYE);
	}

	@Override
	public String getDescription() {
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
