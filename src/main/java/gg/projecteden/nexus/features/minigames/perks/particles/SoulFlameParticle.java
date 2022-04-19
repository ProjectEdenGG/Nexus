package gg.projecteden.nexus.features.minigames.perks.particles;

import gg.projecteden.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SoulFlameParticle implements PlayerParticlePerk {
	@Override
	public @NotNull String getName() {
		return "Soul Flames";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.SOUL_TORCH);
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Burn like the flames of the nether wastes");
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
