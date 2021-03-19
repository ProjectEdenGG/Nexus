package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.minigames.perks.FlameParticle;
import me.pugabyte.nexus.features.minigames.perks.HeartParticle;

@AllArgsConstructor
@Getter
public enum PerkType {
	FLAME_PARTICLE(new FlameParticle()),
	HEART_PARTICLE(new HeartParticle())
	;

	private final Perk perk;

	public String getPermission() {
		return "minigames.perks." + name().toLowerCase();
	}
}
