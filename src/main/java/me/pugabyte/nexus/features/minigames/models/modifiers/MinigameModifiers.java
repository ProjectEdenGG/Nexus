package me.pugabyte.nexus.features.minigames.models.modifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.modifiers.HalfKnockback;
import me.pugabyte.nexus.features.minigames.modifiers.ModernCombat;
import me.pugabyte.nexus.features.minigames.modifiers.MoonGravity;
import me.pugabyte.nexus.features.minigames.modifiers.NoKnockback;
import me.pugabyte.nexus.features.minigames.modifiers.NoModifier;

@Getter
@RequiredArgsConstructor
public enum MinigameModifiers {
	NONE(new NoModifier()),
	MODERN_COMBAT(new ModernCombat()),
	NO_KNOCKBACK(new NoKnockback()),
	HALF_KNOCKBACK(new HalfKnockback()),
	MOON_GRAVITY(new MoonGravity())
	;

	private final MinigameModifier modifier;
}
