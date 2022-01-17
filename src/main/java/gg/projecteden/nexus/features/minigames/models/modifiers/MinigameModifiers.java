package gg.projecteden.nexus.features.minigames.models.modifiers;

import gg.projecteden.nexus.features.minigames.modifiers.BulletArrows;
import gg.projecteden.nexus.features.minigames.modifiers.HalfKnockback;
import gg.projecteden.nexus.features.minigames.modifiers.ModernCombat;
import gg.projecteden.nexus.features.minigames.modifiers.MoonGravity;
import gg.projecteden.nexus.features.minigames.modifiers.NoKnockback;
import gg.projecteden.nexus.features.minigames.modifiers.NoModifier;
import gg.projecteden.nexus.features.minigames.modifiers.SuperSpeed;
import lombok.RequiredArgsConstructor;

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

	public MinigameModifier get() {
		return modifier;
	}

	private final MinigameModifier modifier;
}
