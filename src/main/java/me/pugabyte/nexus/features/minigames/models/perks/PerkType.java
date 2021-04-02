package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.minigames.perks.arrowparticles.BasicTrail;
import me.pugabyte.nexus.features.minigames.perks.loadouts.CreeperSkull;
import me.pugabyte.nexus.features.minigames.perks.loadouts.DragonSkull;
import me.pugabyte.nexus.features.minigames.perks.loadouts.SkeletonSkull;
import me.pugabyte.nexus.features.minigames.perks.loadouts.UnicornHorn;
import me.pugabyte.nexus.features.minigames.perks.loadouts.ZombieSkull;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.Concrete;
import me.pugabyte.nexus.features.minigames.perks.particles.FlameParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.HeartParticle;

@AllArgsConstructor
@Getter
public enum PerkType implements IHasPerkCategory {
	FLAME_PARTICLE(new FlameParticle()),
	HEART_PARTICLE(new HeartParticle()),
	UNICORN_HORN(new UnicornHorn()),
	DRAGON_SKULL(new DragonSkull()),
	CONCRETE(new Concrete()),
	BASIC_TRAIL(new BasicTrail()),
	ZOMBIE_SKULL(new ZombieSkull()),
	SKELETON_SKULL(new SkeletonSkull()),
	CREEPER_SKULL(new CreeperSkull())
	;

	private final Perk perk;

	@Override
	public PerkCategory getPerkCategory() {
		return perk.getPerkCategory();
	}
}
