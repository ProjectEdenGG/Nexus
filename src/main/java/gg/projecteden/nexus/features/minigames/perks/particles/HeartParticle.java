package gg.projecteden.nexus.features.minigames.perks.particles;

import gg.projecteden.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class HeartParticle implements PlayerParticlePerk {
	@Override
	public @NotNull String getName() {
		return "Heart";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.RED_TULIP);
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Someone's falling madly in love with that enemy team...");
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
