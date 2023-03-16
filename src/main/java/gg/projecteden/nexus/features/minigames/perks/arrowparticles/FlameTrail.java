package gg.projecteden.nexus.features.minigames.perks.arrowparticles;

import gg.projecteden.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class FlameTrail implements ParticleProjectilePerk {

	@Override
	public @NotNull String getName() {
		return "Flame Trail";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.FLINT_AND_STEEL);
	}

	@Override
	public int getPrice() {
		return 30;
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Create a spark of fire behind your arrows");
	}

	@Override
	public Particle getParticle() {
		return Particle.SMALL_FLAME;
	}

	@Override
	public double getSpeed() {
		return 0.01d;
	}
}
