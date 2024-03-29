package gg.projecteden.nexus.features.minigames.models.modifiers;

import gg.projecteden.nexus.features.minigames.modifiers.Blindness;
import gg.projecteden.nexus.features.minigames.modifiers.BulletArrows;
import gg.projecteden.nexus.features.minigames.modifiers.HalfKnockback;
import gg.projecteden.nexus.features.minigames.modifiers.JumpBoost;
import gg.projecteden.nexus.features.minigames.modifiers.ModernCombat;
import gg.projecteden.nexus.features.minigames.modifiers.MoonGravity;
import gg.projecteden.nexus.features.minigames.modifiers.NoJumping;
import gg.projecteden.nexus.features.minigames.modifiers.NoKnockback;
import gg.projecteden.nexus.features.minigames.modifiers.NoModifier;
import gg.projecteden.nexus.features.minigames.modifiers.SuperSpeed;
import gg.projecteden.nexus.features.minigames.modifiers.XRun;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MinigameModifiers {
	NONE(new NoModifier()),
	MODERN_COMBAT(new ModernCombat()),
	NO_KNOCKBACK(new NoKnockback()),
	HALF_KNOCKBACK(new HalfKnockback()),
	MOON_GRAVITY(new MoonGravity()),
	BULLET_ARROWS(new BulletArrows()),
	SUPER_SPEED(new SuperSpeed()),
	NO_JUMPING(new NoJumping()),
	BLINDNESS(new Blindness()),
	XRUN(new XRun()),
	JUMP_BOOST(new JumpBoost()),
	;

	public MinigameModifier get() {
		return modifier;
	}

	private final MinigameModifier modifier;
}
