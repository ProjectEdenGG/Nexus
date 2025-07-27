package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.jetbrains.annotations.NotNull;

public class BulletArrows implements MinigameModifier {

	@Override
	public @NotNull String getName() {
		return "Bullet Arrows";
	}

	@Override
	public @NotNull String getDescription() {
		return "Projectiles fire straight forwards like bullets";
	}

}
