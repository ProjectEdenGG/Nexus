package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;

public abstract class ParticleProjectilePerk extends Perk {
    public abstract Particle getParticle();
    public int getSpeed() {
        return 1;
    }
    public void tick(Projectile projectile) {
        ParticleUtils.display(getParticle(), projectile.getLocation(), 1, .02d, .02d, .02d, 2);
    }
}
