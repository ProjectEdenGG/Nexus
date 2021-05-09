package me.pugabyte.nexus.features.minigames.models.modifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.modifiers.BulletArrows;
import me.pugabyte.nexus.features.minigames.modifiers.HalfKnockback;
import me.pugabyte.nexus.features.minigames.modifiers.ModernCombat;
import me.pugabyte.nexus.features.minigames.modifiers.MoonGravity;
import me.pugabyte.nexus.features.minigames.modifiers.NoKnockback;
import me.pugabyte.nexus.features.minigames.modifiers.NoModifier;
import me.pugabyte.nexus.features.minigames.modifiers.SuperSpeed;

@Getter
@RequiredArgsConstructor
public enum MinigameModifiers {
	NONE(new NoModifier()),
	MODERN_COMBAT(new ModernCombat()),
	NO_KNOCKBACK(new NoKnockback()),
	HALF_KNOCKBACK(new HalfKnockback()),
	MOON_GRAVITY(new MoonGravity()),
	BULLET_ARROWS(new BulletArrows()),
	SUPER_SPEED(new SuperSpeed())
	;

	private final MinigameModifier modifier;
}
