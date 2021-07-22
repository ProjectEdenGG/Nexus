package gg.projecteden.nexus.features.minigames.perks.arrowparticles;

import gg.projecteden.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BasicTrail implements ParticleProjectilePerk {
    @Override
    public Particle getParticle() {
        return Particle.END_ROD;
    }

    @Override
    public @NotNull String getName() {
        return "Basic Trail";
    }

    @Override
    public @NotNull ItemStack getMenuItem() {
        return new ItemStack(Material.WHITE_DYE);
    }

    @Override
    public @NotNull String getDescription() {
        return "Give your arrows some sparkles with this simple trail";
    }

    @Override
    public int getPrice() {
        return 10;
    }

    @Override
    public double getSpeed() {
        return 0.01d;
    }
}
