package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.minigames.perks.DragonSkull;
import me.pugabyte.nexus.features.minigames.perks.FlameParticle;
import me.pugabyte.nexus.features.minigames.perks.HeartParticle;
import me.pugabyte.nexus.features.minigames.perks.UnicornHorn;

@AllArgsConstructor
@Getter
public enum PerkType {
	FLAME_PARTICLE(new FlameParticle()),
	HEART_PARTICLE(new HeartParticle()),
	UNICORN_HORN(new UnicornHorn()),
	DRAGON_SKULL(new DragonSkull())
	;

	private final Perk perk;
}
