package gg.projecteden.nexus.features.minigames.perks.particles;

import gg.projecteden.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class FlameParticle implements PlayerParticlePerk {
	@Override
	public @NotNull String getName() {
		return "Flames";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.TORCH);
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Burn like the fire that fuels your craving for blood!");
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
