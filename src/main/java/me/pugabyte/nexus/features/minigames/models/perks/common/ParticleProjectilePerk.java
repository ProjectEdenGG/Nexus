package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;

public abstract class ParticleProjectilePerk extends Perk {
    public abstract Particle getParticle();
    public double getSpeed() {
        return 1d;
    }
    public void tick(Projectile projectile) {
        ParticleUtils.display(getParticle(), projectile.getLocation(), 2, .02d, .02d, .02d, getSpeed());
    }
}
