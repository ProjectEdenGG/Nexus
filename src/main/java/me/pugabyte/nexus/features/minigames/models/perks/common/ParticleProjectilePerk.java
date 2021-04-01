package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwner;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwnerService;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ParticleProjectilePerk extends Perk {
    public abstract Particle getParticle();
    public double getSpeed() {
        return 1d;
    }

    @Override
    public PerkCategory getPerkCategory() {
        return PerkCategory.ARROW_TRAIL;
    }

    public void tick(Projectile projectile, List<Player> players) {
        players = players.stream().filter(player -> {
            PerkOwner owner = new PerkOwnerService().get(player);
            return owner.getHideParticle().showParticle(getPerkCategory());
        }).collect(Collectors.toList());
        new ParticleBuilder(getParticle()).receivers(players).count(2).offset(.02, .02, .02).location(projectile.getLocation()).extra(getSpeed()).spawn();
    }

    public void tick(Projectile projectile, Match match) {
        tick(projectile, match.getMinigamers().stream().map(Minigamer::getPlayer).collect(Collectors.toList()));
    }
}
