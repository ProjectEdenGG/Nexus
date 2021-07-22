package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.jetbrains.annotations.NotNull;

public class NoModifier implements MinigameModifier {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {}

	@Override
	public @NotNull String getName() {
		return "No Modifier";
	}

	@Override
	public @NotNull String getDescription() {
		return "Does nothing";
	}
}
