package gg.projecteden.nexus.features.minigames.models.modifiers;

import gg.projecteden.nexus.features.minigames.mechanics.Bingo;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.features.minigames.modifiers.BingoBlackout;
import gg.projecteden.nexus.features.minigames.modifiers.BingoLockout;
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
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

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
	@IncludeMechanics(Bingo.class)
	BINGO_BLACKOUT(new BingoBlackout()),
	@IncludeMechanics(Bingo.class)
	BINGO_LOCKOUT(new BingoLockout()),
	;

	private final MinigameModifier modifier;

	public MinigameModifier get() {
		return modifier;
	}

	public static MinigameModifiers of(MinigameModifier modifier) {
		for (MinigameModifiers value : values())
			if (value.get() == modifier)
				return value;

		throw new InvalidInputException("No MinigameModifier found for " + modifier);
	}

	public boolean appliesTo(Mechanic mechanic) {
		if (!getField().isAnnotationPresent(IncludeMechanics.class))
			return true;

		var mechanics = getField().getAnnotation(IncludeMechanics.class).value();
		for (Class<? extends Mechanic> included : mechanics)
			if (included.isAssignableFrom(mechanic.getClass()))
				return true;

		return false;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface IncludeMechanics { Class<? extends Mechanic>[] value(); }

	@SneakyThrows
	private Field getField() {
		return getClass().getField(name());
	}

}
