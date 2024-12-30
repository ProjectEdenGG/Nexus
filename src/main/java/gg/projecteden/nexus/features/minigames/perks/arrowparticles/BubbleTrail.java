package gg.projecteden.nexus.features.minigames.perks.arrowparticles;

import gg.projecteden.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BubbleTrail implements ParticleProjectilePerk {

	@Override
	public @NotNull String getName() {
		return "Bubble";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.PRISMARINE);
	}

	@Override
	public int getPrice() {
		return 30;
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Apply bubbles to your arrows");
	}

	@Override
	public Particle getParticle() {
		return Particle.BUBBLE;
	}

	@Override
	public double getSpeed() {
		return 0.01d;
	}
}
