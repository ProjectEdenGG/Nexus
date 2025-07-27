package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.jetbrains.annotations.NotNull;

public class NoJumping implements MinigameModifier {

	@Override
	public @NotNull String getName() {
		return "No Jumping";
	}

	@Override
	public @NotNull String getDescription() {
		return "Disables jumping";
	}

}
