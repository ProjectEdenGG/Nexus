package gg.projecteden.nexus.features.minigames.perks.arrowparticles;

import gg.projecteden.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class PETRail implements ParticleProjectilePerk {
	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.WHITE_GLAZED_TERRACOTTA);
	}

	@Override
	public int getPrice() {
		return 50;
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Rep the server colors on your arrows");
	}

	@Override
	public Particle getParticle() {
		return Particle.DUST;
	}

	@Override
	public @NotNull String getName() {
		return "Eden";
	}

	@Override
	public double getSpeed() {
		return 0.01;
	}

	static DustOptions cyan = new DustOptions(ColorType.CYAN.getBukkitColor(), 1);
	static DustOptions yellow = new DustOptions(ColorType.YELLOW.getBukkitColor(), 1);

	@Override
	public Particle.DustOptions getDustOptions(@NotNull Projectile projectile) {
		return LocalTime.now().getSecond() % 2 == 0 ? cyan : yellow;
	}

}
