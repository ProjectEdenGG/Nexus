package me.pugabyte.nexus.features.minigames.perks.particles;

import me.pugabyte.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class HeartParticle extends PlayerParticlePerk {
	@Override
	public String getName() {
		return "Heart";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.RED_TULIP);
	}

	@Override
	public String getDescription() {
		return "Someone's falling madly in love with that enemy team...";
	}

	@Override
	public int getPrice() {
		return 20;
	}

	@Override
	public Particle getParticle() {
		return Particle.HEART;
	}

	@Override
	public double getSpeed() {
		return 0.7d;
	}

	@Override
	public int getCount() {
		return 2;
	}
}
