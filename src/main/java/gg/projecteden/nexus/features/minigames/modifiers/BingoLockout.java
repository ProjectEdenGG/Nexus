package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.jetbrains.annotations.NotNull;

public class BingoLockout implements MinigameModifier {

	@Override
	public @NotNull String getName() {
		return "Bingo Lockout";
	}

	@Override
	public @NotNull String getDescription() {
		return "Completing a challenge prevents others from completing it";
	}

}
